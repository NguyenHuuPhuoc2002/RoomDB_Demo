package com.example.roomdatabase_

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase_.adapter.UserAdapter
import com.example.roomdatabase_.database.UserDatabase
import com.example.roomdatabase_.`interface`.OnItemClickListener
import com.example.roomdatabase_.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object{
        const val MY_REQUEST_CODE: Int = 10
    }
    private lateinit var edtUsername: EditText
    private lateinit var edtAddress: EditText
    private lateinit var edtYear: EditText
    private lateinit var edtSearch: EditText
    private lateinit var btnAddUser: Button
    private lateinit var tvDeleteAll: TextView
    private lateinit var rvUser: RecyclerView
    private var userAdapter: UserAdapter? = null
    private var mListUser: List<User> = ArrayList<User>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        runBlocking {
            loadData()
        }

        setAdapter()
        btnAddUser.setOnClickListener {
            addUser()
        }
        tvDeleteAll.setOnClickListener {
            deleteAll()
        }
        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearchUser()
            }
            false
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchUser() {
        val strKey = edtSearch.text.toString().trim()
        mListUser = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
            mListUser = UserDatabase.getInstance(this@MainActivity).userDao().searchUser(strKey)
            Log.d("size", mListUser.size.toString())
            withContext(Dispatchers.Main){
                setAdapter()
                userAdapter?.notifyDataSetChanged()
            }
        }
        hideSoftKeyboard()
    }


    private fun deleteAll() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Xác nhận xóa")
        alertDialogBuilder.setMessage("Bạn có muốn xóa tất cả không?")

        alertDialogBuilder.setPositiveButton("Có"){ dialog: DialogInterface, _: Int ->
            CoroutineScope(Dispatchers.IO).launch {
                UserDatabase.getInstance(this@MainActivity).userDao().deleteAllUser()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Xóa thành công !", Toast.LENGTH_SHORT).show()
                    runBlocking {
                        loadData()
                        setAdapter()
                    }
                }
            }
        }
        alertDialogBuilder.setNegativeButton("Không"){dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun init(){
        edtUsername = findViewById(R.id.edt_username)
        edtAddress = findViewById(R.id.edt_address)
        edtYear = findViewById(R.id.edt_year)
        btnAddUser = findViewById(R.id.btn_add_user)
        tvDeleteAll = findViewById(R.id.tv_delete_all)
        edtSearch = findViewById(R.id.edt_search)
        rvUser = findViewById(R.id.rv_user)

    }
    private fun setAdapter(){
        userAdapter = UserAdapter(mListUser, object : OnItemClickListener{
            override fun updateUser(user: User) {
                clickUpdateUser(user)
            }

            override fun deleteUser(user: User) {
                clickDeleteUser(user)
            }
        } )
        rvUser.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        rvUser.adapter = userAdapter
    }

    private fun clickDeleteUser(user: User) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Xác nhận xóa")
        alertDialogBuilder.setMessage("Bạn có muốn xóa ${user.username} không?")

        alertDialogBuilder.setPositiveButton("Có"){ _: DialogInterface, _: Int ->
            CoroutineScope(Dispatchers.IO).launch {
                UserDatabase.getInstance(this@MainActivity).userDao().deleteUser(user)
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Xóa thành công !", Toast.LENGTH_SHORT).show()
                    runBlocking {
                        loadData()
                        setAdapter()
                    }
                }
            }
        }
        alertDialogBuilder.setNegativeButton("Không"){dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun clickUpdateUser(user: User) {
        val intent = Intent(this@MainActivity, UpdateActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("object_user", user)
        intent.putExtras(bundle)
        startActivityForResult(intent, MY_REQUEST_CODE)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addUser() {
        val strUsername = edtUsername.text.toString().trim()
        val strAddress = edtAddress.text.toString().trim()
        val strYear = edtYear.text.toString().trim()
        val user = User(username = strUsername, address = strAddress, year = strYear)

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            if(isUserExist(user)){
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "User exist", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            UserDatabase.getInstance(this@MainActivity).userDao().insertUser(user)
            withContext(Dispatchers.Main) {
                Log.d("size", mListUser.size.toString())
                loadData()
                setAdapter()
                userAdapter?.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Add user successfully", Toast.LENGTH_SHORT).show()
                edtUsername.setText("")
                edtAddress.setText("")
                edtYear.setText("")
                hideSoftKeyboard()
            }
        }
    }

    private suspend fun isUserExist(user: User): Boolean{
        val list: List<User> = UserDatabase.getInstance(this).userDao().checkUsername(user.username)
        return list.isNotEmpty()
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadData(){
        mListUser = UserDatabase.getInstance(this@MainActivity).userDao().getListUser()
        userAdapter?.notifyDataSetChanged()
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            runBlocking {
                loadData()
                setAdapter()

            }
        }
    }

}

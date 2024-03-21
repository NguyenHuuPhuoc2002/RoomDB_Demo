package com.example.roomdatabase_

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.roomdatabase_.database.UserDatabase
import com.example.roomdatabase_.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateActivity : AppCompatActivity() {
    private lateinit var edtUsername: EditText
    private lateinit var edtAddress: EditText
    private lateinit var btnUpdateUser: Button
    private lateinit var mUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        init()
        mUser = intent.extras?.get("object_user") as User
        if(mUser != null){
            edtUsername.setText(mUser.username)
            edtAddress.setText(mUser.address)
        }
        btnUpdateUser.setOnClickListener {
            updateUser()
        }

    }

    private fun updateUser() {
        val strUsername = edtUsername.text.toString().trim()
        val strAddress = edtAddress.text.toString().trim()

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            mUser.username = strUsername
            mUser.address = strAddress
            UserDatabase.getInstance(this@UpdateActivity).userDao().updateUser(mUser)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UpdateActivity, "Update Succsess", Toast.LENGTH_SHORT).show()
            }

        }
        val intentResult = Intent()
        setResult(Activity.RESULT_OK, intentResult)
        finish()
    }

    private fun init(){
        edtUsername = findViewById(R.id.edt_username)
        edtAddress = findViewById(R.id.edt_address)
        btnUpdateUser = findViewById(R.id.btn_update_user)
    }
}
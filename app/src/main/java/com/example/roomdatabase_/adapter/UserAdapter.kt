package com.example.roomdatabase_.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase_.R
import com.example.roomdatabase_.`interface`.OnItemClickListener
import com.example.roomdatabase_.model.User

class UserAdapter(private val mList: List<User>, val iclickItemUser: OnItemClickListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = mList[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tv_username).text = currentItem.username
            findViewById<TextView>(R.id.tv_address).text = currentItem.address
            findViewById<TextView>(R.id.tv_year).text = currentItem.year

            findViewById<Button>(R.id.btn_update).setOnClickListener {
                iclickItemUser.updateUser(mList[position])
            }
            findViewById<Button>(R.id.btn_delete).setOnClickListener {
                iclickItemUser.deleteUser(mList[position])
            }
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}

package com.example.roomdatabase_.`interface`

import com.example.roomdatabase_.model.User

interface OnItemClickListener {
    fun updateUser(user: User)
    fun deleteUser(user: User)
}
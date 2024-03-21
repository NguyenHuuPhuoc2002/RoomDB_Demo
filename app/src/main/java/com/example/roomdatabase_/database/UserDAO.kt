package com.example.roomdatabase_.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.roomdatabase_.model.User

@Dao
interface UserDAO {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getListUser(): List<User>

    @Query("SELECT * FROM user WHERE username = :user")
    suspend fun checkUsername(user: String): List<User>

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUser()

    @Query("SELECT * FROM user WHERE username LIKE '%' || :name || '%'")
    fun searchUser(name: String): List<User>
}
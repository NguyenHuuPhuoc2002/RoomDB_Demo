package com.example.roomdatabase_.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
class User(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var username: String,
        var address: String,
        var year: String
): Serializable {
}
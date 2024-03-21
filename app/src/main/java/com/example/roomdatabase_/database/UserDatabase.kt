package com.example.roomdatabase_.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomdatabase_.model.User

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {
    companion object{
        val migration_from_1_to_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user ADD COLUMN year TEXT NOT NULL DEFAULT ''")
            }
        }
        private const val DATABASE_NAME: String = "user.db"
        private var INSTANCES: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            if (INSTANCES == null) {
                synchronized(this){
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        DATABASE_NAME
                    ).addMigrations(migration_from_1_to_2).build()
                    INSTANCES = instance
                    return instance
                }
            }
            return INSTANCES!!
        }
    }

    abstract fun userDao(): UserDAO
}
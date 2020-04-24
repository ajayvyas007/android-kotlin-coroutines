package com.example.ajay.corotines.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ajay.corotines.data.local.dao.UserDao
import com.example.ajay.corotines.data.local.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}
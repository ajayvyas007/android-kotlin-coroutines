package com.example.ajay.corotines.data.local

import com.example.ajay.corotines.data.local.entity.User

interface DatabaseHelper {

    suspend fun getUsers(): List<User>

    suspend fun insertAll(users: List<User>)

}
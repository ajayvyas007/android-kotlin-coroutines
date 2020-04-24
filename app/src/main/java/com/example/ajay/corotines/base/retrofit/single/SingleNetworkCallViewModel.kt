package com.example.ajay.corotines.base.retrofit.single

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ajay.corotines.data.api.ApiHelper
import com.example.ajay.corotines.data.local.DatabaseHelper
import com.example.ajay.corotines.data.model.ApiUser
import com.example.ajay.corotines.utils.Resource
import kotlinx.coroutines.launch

class SingleNetworkCallViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getUsers()
                users.postValue(Resource.success(usersFromApi))
            } catch (e: Exception) {
                users.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}
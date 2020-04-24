package com.example.ajay.corotines.base.errorhandling.supervisor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ajay.corotines.data.api.ApiHelper
import com.example.ajay.corotines.data.local.DatabaseHelper
import com.example.ajay.corotines.data.model.ApiUser
import com.example.ajay.corotines.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class IgnoreErrorAndContinueViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            try {
                // supervisorScope is needed, so that we can ignore error and continue
                // here, more than two child jobs are running in parallel under a supervisor, one child job gets failed, we can continue with other.
                supervisorScope {
                    val usersFromApiDeferred = async { apiHelper.getUsersWithError() }
                    val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }

                    val usersFromApi = try {
                        usersFromApiDeferred.await()
                    } catch (e: Exception) {
                        emptyList<ApiUser>()
                    }

                    val moreUsersFromApi = try {
                        moreUsersFromApiDeferred.await()
                    } catch (e: Exception) {
                        emptyList<ApiUser>()
                    }

                    val allUsersFromApi = mutableListOf<ApiUser>()
                    allUsersFromApi.addAll(usersFromApi)
                    allUsersFromApi.addAll(moreUsersFromApi)

                    users.postValue(Resource.success(allUsersFromApi))
                }
            } catch (e: Exception) {
                users.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}
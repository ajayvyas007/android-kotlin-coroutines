package com.example.ajay.corotines.base.retrofit.single

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.ajay.corotines.data.api.ApiHelper
import com.example.ajay.corotines.data.local.DatabaseHelper
import com.example.ajay.corotines.data.model.ApiUser
import com.example.ajay.corotines.utils.Resource
import com.example.ajay.corotines.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SingleNetworkCallViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var databaseHelper: DatabaseHelper

    @Mock
    private lateinit var apiUsersObserver: Observer<Resource<List<ApiUser>>>

    private lateinit var viewModel: SingleNetworkCallViewModel

    @Before
    fun setUp() {
        viewModel = SingleNetworkCallViewModel(apiHelper, databaseHelper)
        viewModel.getUsers().observeForever(apiUsersObserver)
    }

    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        testCoroutineRule.runBlockingTest {
            doReturn(emptyList<ApiUser>())
                .`when`(apiHelper)
                .getUsers()
            viewModel.fetchUsers()
            verify(apiHelper).getUsers()
            verify(apiUsersObserver).onChanged(Resource.success(emptyList()))
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            doReturn(Exception())
                .`when`(apiHelper)
                .getUsers()
            viewModel.fetchUsers()
            verify(apiHelper).getUsers()
            verify(apiUsersObserver).onChanged(Resource.error("Something Went Wrong", null))
        }
    }

    @After
    fun tearDown() {
        viewModel.getUsers().removeObserver(apiUsersObserver)
    }

}
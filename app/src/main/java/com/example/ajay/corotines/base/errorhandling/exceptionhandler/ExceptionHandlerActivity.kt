package com.example.ajay.corotines.base.errorhandling.exceptionhandler

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajay.corotines.R
import com.example.ajay.corotines.data.api.ApiHelperImpl
import com.example.ajay.corotines.data.api.RetrofitBuilder
import com.example.ajay.corotines.data.local.DatabaseBuilder
import com.example.ajay.corotines.data.local.DatabaseHelperImpl
import com.example.ajay.corotines.data.model.ApiUser
import com.example.ajay.corotines.base.adapter.ApiUserAdapter
import com.example.ajay.corotines.utils.Status
import com.example.ajay.corotines.utils.ViewModelFactory
import kotlinx.android.synthetic.main.activity_recycler_view.*

class ExceptionHandlerActivity : AppCompatActivity() {

    private lateinit var viewModel: ExceptionHandlerViewModel
    private lateinit var adapter: ApiUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        setupUI()
        setupViewModel()
        setupAPICall()
    }

    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            ApiUserAdapter(
                arrayListOf()
            )
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupAPICall() {
        viewModel.getUsers().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { users -> renderList(users) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.fetchUsers()
    }

    private fun renderList(users: List<ApiUser>) {
        adapter.addData(users)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(ExceptionHandlerViewModel::class.java)
    }
}

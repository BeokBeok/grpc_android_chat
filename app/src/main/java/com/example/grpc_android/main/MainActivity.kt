package com.example.grpc_android.main

import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.R
import com.example.grpc_android.base.BaseActivity
import com.example.grpc_android.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun setupViewModel() {
        binding.vm = viewModel
    }

    override fun setupObserve() {
        val owner = this

        viewModel.run {
            receive.observe(owner, Observer {
                println("receive it $it")
            })
            cid.observe(owner, Observer {
                println("cid is $it")
                binding.tietCid.setText(it)
            })
            errMsg.observe(owner, Observer {
                Toast.makeText(owner, it, Toast.LENGTH_SHORT).show()
            })
            output.observe(owner, Observer {
                println("output is $it")
            })
        }
    }
}
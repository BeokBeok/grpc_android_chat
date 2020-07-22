package com.example.grpc_android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.databinding.ActivityMainBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupObserve()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                lifecycleOwner = this@MainActivity
                vm = viewModel
            }
    }

    private fun setupObserve() {
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
package com.example.grpc_android.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.R
import com.example.grpc_android.base.BaseActivity
import com.example.grpc_android.databinding.ActivityMainBinding
import com.example.grpc_android.ext.startActivity
import com.example.grpc_android.room.ChatRoomListActivity
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnGetRooms.setOnClickListener {
            startActivity<ChatRoomListActivity>(bundleOf("uid" to binding.tietUid.text.toString()))
        }
    }

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
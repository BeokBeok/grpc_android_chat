package com.example.grpc_android.room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.R
import com.example.grpc_android.base.BaseActivity
import com.example.grpc_android.base.BaseSingleAdapter
import com.example.grpc_android.databinding.ActivityChatRoomListBinding
import javax.inject.Inject

class ChatRoomListActivity : BaseActivity<ActivityChatRoomListBinding>(
    R.layout.activity_chat_room_list
) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<ChatRoomListViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        showContents()
    }

    private fun setupRecyclerView() {
        binding.rvChatRoomListContents.adapter = BaseSingleAdapter<String>(
            layoutId = R.layout.item_chat_room_list,
            itemBindingId = BR.item,
            viewModels = mapOf(BR.vm to viewModel)
        )
    }

    private fun showContents() {
        viewModel.getRooms(intent.extras?.get("uid") as? String ?: "")
    }

    override fun setupViewModel() {
        binding.vm = viewModel
    }
}
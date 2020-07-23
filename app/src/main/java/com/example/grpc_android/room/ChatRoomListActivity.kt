package com.example.grpc_android.room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.R
import com.example.grpc_android.base.BaseActivity
import com.example.grpc_android.base.BaseSingleAdapter
import com.example.grpc_android.databinding.ActivityChatRoomListBinding
import com.example.grpc_android.ext.startActivity
import com.example.grpc_android.talk.TalkActivity
import javax.inject.Inject

class ChatRoomListActivity : BaseActivity<ActivityChatRoomListBinding>(
    R.layout.activity_chat_room_list
) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<ChatRoomListViewModel> { viewModelFactory }

    private lateinit var uid: String

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
        uid = intent.extras?.get("uid") as? String ?: ""
        viewModel.getRooms(uid)
    }

    override fun setupViewModel() {
        binding.vm = viewModel
    }

    override fun setupObserve() {
        val owner = this
        viewModel.run {
            roomSelect.observe(owner, Observer {
                startActivity<TalkActivity>(
                    bundleOf(
                        "uid" to uid,
                        "cid" to it
                    )
                )
            })
            receive.observe(owner, Observer {
                println("receive $it")
            })
        }
    }
}
package com.example.grpc_android.talk

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import com.example.grpc_android.R
import com.example.grpc_android.base.BaseActivity
import com.example.grpc_android.base.BaseViewTypeAdapter
import com.example.grpc_android.base.ViewHolderType
import com.example.grpc_android.databinding.ActivityTalkBinding
import com.example.grpc_android.talk.model.MessageData
import javax.inject.Inject

class TalkActivity : BaseActivity<ActivityTalkBinding>(R.layout.activity_talk) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<TalkViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        showContents()
    }

    private fun showContents() {
        viewModel.getMessages(intent.extras?.get("cid") as? String ?: "")
    }

    private fun setupRecyclerView() {
        val viewHolderMapper: (Any) -> ViewHolderType = {
            when ((it as MessageData).userId) {
                intent.extras?.get("uid") as? String ?: "" -> TalkViewHolderType.CHAT_MY_MESSAGE
                else -> TalkViewHolderType.CHAT_MESSAGE
            }
        }
        binding.rvTalkContents.adapter = BaseViewTypeAdapter<MessageData>(
            viewHolderMapper = viewHolderMapper,
            viewHolderType = TalkViewHolderType::class,
            viewModels = mapOf(BR.vm to viewModel)
        )
    }

    override fun setupViewModel() {
        binding.vm = viewModel
    }

    enum class TalkViewHolderType(
        override val layoutId: Int,
        override val itemBindingId: Int
    ) : ViewHolderType {
        CHAT_MESSAGE(R.layout.item_talk_message, BR.item),
        CHAT_MY_MESSAGE(R.layout.item_talk_my_message, BR.item)
    }
}
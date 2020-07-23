package com.example.grpc_android.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.talk.model.MessageData
import com.example.grpc_android.talk.model.mapToPresenter
import com.example.grpc_android.util.ChatEventReceiver
import io.grpc.chat.Receive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class TalkViewModel @Inject constructor(
    chatEventReceiver: ChatEventReceiver,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val receive: LiveData<Receive> = chatEventReceiver.receive

    private val _messageList = MutableLiveData<List<MessageData>>()
    val messageList: LiveData<List<MessageData>> get() = _messageList

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.value = throwable.message
    }

    fun getMessages(cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.getMessages(cid = cid)
        if (result.isSuccess) {
            _messageList.value =
                result.getOrNull()?.resp?.payload?.messages?.messagesList?.map { it.mapToPresenter() }
                    ?: emptyList()
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }
}
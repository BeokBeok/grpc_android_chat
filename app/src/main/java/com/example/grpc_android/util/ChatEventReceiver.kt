package com.example.grpc_android.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.grpc_android.data.ChatRepository
import io.grpc.chat.Receive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatEventReceiver @Inject constructor(private val chatRepository: ChatRepository) {

    private val _receive = MutableLiveData<Receive>()
    val receive: LiveData<Receive> get() = _receive

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.postValue(throwable.message)
    }

    fun eventListen(uid: String) =
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
            chatRepository.eventListen(uid = uid).collect {
                _receive.postValue(it)
            }
        }
}
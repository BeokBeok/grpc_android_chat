package com.example.grpc_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import io.grpc.chat.Receive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {

    private val _receive = MutableLiveData<Receive>()
    val receive: LiveData<Receive> get() = _receive

    private val _cid = MutableLiveData<String>()
    val cid: LiveData<String> get() = _cid

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun eventListen(uid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        chatRepository.eventListen(uid).flowOn(Dispatchers.IO).collect {
            _receive.value = it
        }
    }

    fun chatWith(uid: String, peerName: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatWith(uid, peerName)
        if (result.isSuccess) {
            _cid.value = result.getOrNull()?.resp?.cid
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }

}
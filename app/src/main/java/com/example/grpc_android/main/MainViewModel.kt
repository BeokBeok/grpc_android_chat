package com.example.grpc_android.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.util.ChatEventReceiver
import io.grpc.chat.Receive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val chatEventReceiver: ChatEventReceiver,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val receive: LiveData<Receive> = chatEventReceiver.receive

    private val _cid = MutableLiveData<String>()
    val cid: LiveData<String> get() = _cid

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.value = throwable.message
    }

    fun eventListen(uid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        chatEventReceiver.eventListen(uid = uid)
    }

    fun chatWith(uid: String, peerName: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatWith(uid = uid, peerName = peerName)
        if (result.isSuccess) {
            _cid.value = result.getOrNull()?.cid
            println("chatWith is ${result.getOrNull()}")
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun chatIn(uid: String, cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatIn(uid = uid, cid = cid)
        if (result.isSuccess) {
            println("chatIn is ${result.getOrNull()}")
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun chatOut(uid: String, cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatOut(uid = uid, cid = cid)
        if (result.isSuccess) {
            println("chatOut is ${result.getOrNull()}")
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun sendMessage(uid: String, cid: String, msg: String) =
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = chatRepository.sendMessage(uid = uid, cid = cid, msg = msg)
            if (result.isSuccess) {
                println("sendMessage is ${result.getOrNull()}")
            } else {
                _errMsg.value = result.getOrThrow().error.message
            }
        }

    fun getMessages(cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.getMessages(cid = cid)
        if (result.isSuccess) {
            println("getMessage is ${result.getOrNull()}")
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }
}
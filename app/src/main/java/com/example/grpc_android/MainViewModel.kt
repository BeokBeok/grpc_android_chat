package com.example.grpc_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import io.grpc.chat.Receive
import io.grpc.chat.ResponseWithError
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

    private val _output = MutableLiveData<ResponseWithError>()
    val output: LiveData<ResponseWithError> get() = _output

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun eventListen(uid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        chatRepository.eventListen(uid = uid).flowOn(Dispatchers.IO).collect {
            _receive.value = it
        }
    }

    fun chatWith(uid: String, peerName: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatWith(uid = uid, peerName = peerName)
        if (result.isSuccess) {
            _cid.value = result.getOrNull()?.resp?.cid
            _output.value = result.getOrNull()
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }

    fun chatIn(uid: String, cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatIn(uid = uid, cid = cid)
        if (result.isSuccess) {
            _output.value = result.getOrNull()
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }

    fun chatOut(uid: String, cid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.chatOut(uid = uid, cid = cid)
        if (result.isSuccess) {
            _output.value = result.getOrNull()
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }

}
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun eventListen(uid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        chatRepository.eventListen(uid).flowOn(Dispatchers.IO).collect {
            _receive.value = it
        }
    }

}
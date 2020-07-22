package com.example.grpc_android.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomListViewModel @Inject constructor(private val chatRepository: ChatRepository) :
    ViewModel() {

    private val _roomList = MutableLiveData<List<String>>()
    val roomList: LiveData<List<String>> get() = _roomList

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.value = throwable.message
    }

    fun getRooms(uid: String) = viewModelScope.launch {
        if (uid.isEmpty()) return@launch

        val result = chatRepository.getRooms(uid = uid)
        if (result.isSuccess) {
            _roomList.value = result.getOrNull()?.resp?.payload?.rooms?.cidsList ?: emptyList()
        } else {
            _errMsg.value = result.getOrThrow().error.result
        }
    }
}
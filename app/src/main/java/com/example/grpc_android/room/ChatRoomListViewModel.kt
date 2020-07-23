package com.example.grpc_android.room

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

class ChatRoomListViewModel @Inject constructor(
    chatEventReceiver: ChatEventReceiver,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val receive: LiveData<Receive> = chatEventReceiver.receive

    private val _roomList = MutableLiveData<List<String>>()
    val roomList: LiveData<List<String>> get() = _roomList

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.value = throwable.message
    }

    private val _roomSelect = MutableLiveData<String>()
    val roomSelect: LiveData<String> get() = _roomSelect

    fun onClick(cid: String) {
        _roomSelect.value = cid
    }

    fun getRooms(uid: String) = viewModelScope.launch(coroutineExceptionHandler) {
        if (uid.isEmpty()) return@launch

        val result = chatRepository.getRooms(uid = uid)
        if (result.isSuccess) {
            _roomList.value = result.getOrNull()?.cidsList ?: emptyList()
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }
}
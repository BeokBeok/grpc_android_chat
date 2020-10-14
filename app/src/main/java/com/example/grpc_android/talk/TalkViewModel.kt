package com.example.grpc_android.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.talk.vo.MessageVO
import com.example.grpc_android.talk.vo.mapToPresenter
import com.example.grpc_android.util.ChatEventReceiver
import io.grpc.chat.EventType
import io.grpc.chat.Receive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class TalkViewModel @Inject constructor(
    chatEventReceiver: ChatEventReceiver,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val receive: LiveData<Receive> = chatEventReceiver.receive

    private val _messageList = MutableLiveData<List<MessageVO>>()
    val messageList: LiveData<List<MessageVO>> get() = _messageList

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val messages = mutableListOf<MessageVO>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _errMsg.value = throwable.message
    }

    private val _successSendMessage = MutableLiveData<Boolean>()
    val successSendMessage: LiveData<Boolean> get() = _successSendMessage

    private lateinit var uid: String
    private lateinit var cid: String

    fun setupIds(uid: String, cid: String) {
        this.uid = uid
        this.cid = cid
    }

    fun getMessages() = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.getMessages(cid = cid)
        if (result.isSuccess) {
            setupMessages(result.getOrNull()?.map { it.mapToPresenter() } ?: emptyList())
        } else {
            _errMsg.value = result.exceptionOrNull()?.message
        }
    }

    fun sendMessage(msg: String) = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.sendMessage(uid = uid, cid = cid, msg = msg)
        if (result.isSuccess) {
            updateMessage(result.getOrNull()?.message?.mapToPresenter() ?: return@launch)
            chatRepository.saveMessage(cid, result.getOrNull()?.message)
            _messageList.value = messages
            _successSendMessage.value = true
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun receiveMessage(receive: Receive) = viewModelScope.launch(coroutineExceptionHandler) {
        if (receive.eventTypeValue != EventType.MESSAGE_VALUE) return@launch
        val receivedMessage = receive.event.message
        updateMessage(receivedMessage.mapToPresenter())
        chatRepository.saveMessage(cid, receivedMessage)
        _messageList.value = messages
    }

    fun syncLogs() = viewModelScope.launch(coroutineExceptionHandler) {
        val result = chatRepository.syncLogs(uid = uid, cid = cid)
        if (result.isSuccess) {
            setupMessages(result.getOrNull()?.map { it.mapToPresenter() } ?: emptyList())
        } else {
            _errMsg.value = result.exceptionOrNull()?.message
        }
    }

    private fun setupMessages(data: List<MessageVO>) {
        data.forEach { updateMessage(it) }
        _messageList.value = messages
    }

    private fun updateMessage(message: MessageVO) {
        if (messages.isEmpty()) {
            message.isShowProfile = true
            message.isShowHourMinute = true
            messages.add(message)
            return
        }
        if (messages.map { it.lid }.contains(message.lid)) return
        if (!messages.last().isEqualDate(message)) {
            messages.add(MessageVO(date = message.date))
        }
        setupProfileAndDateVisibility(current = messages.last(), next = message)
        messages.add(message)
    }

    private fun setupProfileAndDateVisibility(current: MessageVO, next: MessageVO) {
        if (current.isEqualUid(next) && current.isEqualHourMinute(next)) {
            current.isShowHourMinute = false
            next.isShowProfile = false
        }
    }
}

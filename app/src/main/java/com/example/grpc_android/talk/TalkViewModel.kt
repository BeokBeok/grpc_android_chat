package com.example.grpc_android.talk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.talk.model.MessageData
import com.example.grpc_android.talk.model.mapToPresenter
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

    private val _messageList = MutableLiveData<List<MessageData>>()
    val messageList: LiveData<List<MessageData>> get() = _messageList

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

    private val messages = mutableListOf<MessageData>()

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
        val result = chatRepository.getMessages(uid = uid, cid = cid)
        if (result.isSuccess) {
            val data =
                result.getOrNull()?.messagesList?.map { it.mapToPresenter() } ?: return@launch
            if (data.isEmpty()) return@launch
            setupMessages(data)
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun sendMessage(msg: String) =
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = chatRepository.sendMessage(uid = uid, cid = cid, msg = msg)
            if (result.isSuccess) {
                refreshMessage(result.getOrNull()?.message?.mapToPresenter() ?: return@launch)
                _successSendMessage.value = true
            } else {
                _errMsg.value = result.getOrThrow().error.message
            }
        }

    fun updateMessage(receive: Receive) = viewModelScope.launch(coroutineExceptionHandler) {
        if (receive.eventTypeValue != EventType.MESSAGE_VALUE) return@launch
        refreshMessage(receive.event.message.mapToPresenter())
    }

    private fun setupMessages(data: List<MessageData>) {
        messages.add(MessageData(date = data[0].date))
        data.forEachIndexed { index, messageData ->
            if (index - 1 < 0) return@forEachIndexed
            if (!messageData.isEqualDate(data[index - 1])) {
                messages.add(MessageData(date = messageData.date))
            }
            if (index < data.lastIndex) {
                setupProfileAndDateVisibility(current = messageData, next = data[index + 1])
            } else {
                data[index - 1].isShowHourMinute = false
                messageData.isShowProfile = false
            }
            messages.add(messageData)
        }
        _messageList.value = messages
    }

    private fun refreshMessage(message: MessageData) {
        if (!messages.last().isEqualDate(message)) {
            messages.add(MessageData(date = message.date))
        }

        if (messages.isEmpty()) {
            message.isShowProfile = true
            message.isShowHourMinute = true
        } else {
            setupProfileAndDateVisibility(current = messages.last(), next = message)
        }

        messages.add(message)
        _messageList.value = messages
    }

    private fun setupProfileAndDateVisibility(current: MessageData, next: MessageData) {
        if (current.isEqualUid(next) && current.isEqualHourMinute(next)) {
            current.isShowHourMinute = false
            next.isShowProfile = false
        }
    }
}
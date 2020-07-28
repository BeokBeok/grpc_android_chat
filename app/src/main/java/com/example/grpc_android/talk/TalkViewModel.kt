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
            val data = result.getOrNull()?.messagesList?.map { it.mapToPresenter() }
            if (data.isNullOrEmpty()) return@launch

            setupMessages(data)
            setupProfileIfAvailable()
            setupHeadline()
            _messageList.value = messages
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    private fun setupMessages(data: List<MessageData>) {
        messages.add(MessageData(date = data[0].date))
        data.forEachIndexed { index, messageData ->
            if (index + 1 > data.lastIndex) return@forEachIndexed
            if (messageData.isEqualUid(data[index + 1]) && messageData.hourMinute == data[index + 1].hourMinute) {
                messageData.isShowHourMinute = false
            }
        }
        messages.addAll(data)
    }

    fun sendMessage(msg: String) =
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = chatRepository.sendMessage(uid = uid, cid = cid, msg = msg)
            if (result.isSuccess) {
                val data = result.getOrNull()?.message?.mapToPresenter() ?: return@launch
                if (messages.last()
                        .isEqualUid(data) && messages.last().hourMinute == data.hourMinute
                ) {
                    messages.last().isShowHourMinute = false
                }
                messages.add(data)
                _messageList.value = messages
                _successSendMessage.value = true
            } else {
                _errMsg.value = result.getOrThrow().error.message
            }
        }

    fun updateMessage(receive: Receive) = viewModelScope.launch(coroutineExceptionHandler) {
        if (receive.eventTypeValue != EventType.MESSAGE_VALUE) return@launch

        val receivedMessage = receive.event.message.mapToPresenter()
        if (messages.last()
                .isEqualUid(receivedMessage) && messages.last().hourMinute == receivedMessage.hourMinute
        ) {
            messages.last().isShowHourMinute = false
            receivedMessage.isShowProfile = false
        }
        messages.add(receivedMessage)
        _messageList.value = messages
    }

    private fun setupProfileIfAvailable() {
        messages.forEachIndexed { index, messageData ->
            if (index == 0) return@forEachIndexed
            if (index + 1 > messages.lastIndex) return@forEachIndexed
            if (messageData.isEqualUid(messages[index + 1]) && messageData.hourMinute == messages[index + 1].hourMinute) {
                messages[index + 1].isShowProfile = false
            }
        }
    }

    private fun setupHeadline() {
        val indexToDateList = mutableListOf<Pair<Int, String>>()
        messages.forEachIndexed { index, messageData ->
            if (index + 1 > messages.lastIndex) return@forEachIndexed
            val (currentDate, nextDate) = messageData.date to messages[index + 1].date
            if (currentDate != nextDate) {
                indexToDateList.add(Pair(index + 1, nextDate))
            }
        }
        for (i in indexToDateList.indices) {
            messages.add(
                indexToDateList[i].first + i,
                MessageData(date = indexToDateList[i].second)
            )
        }
    }
}
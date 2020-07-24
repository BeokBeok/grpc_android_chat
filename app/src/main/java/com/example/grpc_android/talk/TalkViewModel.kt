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
            result.getOrNull()?.messagesList?.map { it.mapToPresenter() }?.let {
                messages.add(MessageData(date = it[0].date))
                messages.addAll(it)
            }
            setupProfileAvailable()
            setupHeadline()
            _messageList.value = messages
        } else {
            _errMsg.value = result.getOrThrow().error.message
        }
    }

    fun sendMessage(msg: String) =
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = chatRepository.sendMessage(uid = uid, cid = cid, msg = msg)
            if (result.isSuccess) {
                result.getOrNull()?.message?.mapToPresenter()?.let {
                    messages.add(it)
                    _messageList.value = messages
                }
                _successSendMessage.value = true
            } else {
                _errMsg.value = result.getOrThrow().error.message
            }
        }

    fun updateMessage(receive: Receive) = viewModelScope.launch(coroutineExceptionHandler) {
        if (receive.eventTypeValue == EventType.MESSAGE_VALUE) {
            messages.add(receive.event.message.mapToPresenter())
            _messageList.value = messages
        }
    }

    private fun setupProfileAvailable() {
        messages.forEachIndexed { index, messageData ->
            if (index == 0) return@forEachIndexed
            if (index + 1 > messages.lastIndex) return@forEachIndexed
            if (messageData.hourMinute == messages[index + 1].hourMinute) {
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
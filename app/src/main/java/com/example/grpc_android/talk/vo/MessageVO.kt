package com.example.grpc_android.talk.vo

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.util.TimeConverter
import io.grpc.chat.Message

data class MessageVO(
    val uid: String = "",
    val date: String = "",
    val hourMinute: String = "",
    val message: String = "",
    val transactionId: String = "",
    val lid: String = "",
    var isShowProfile: Boolean = true,
    var isShowHourMinute: Boolean = true
) {

    fun isEqualUid(messageVO: MessageVO): Boolean = uid == messageVO.uid

    fun isEqualHourMinute(messageVO: MessageVO): Boolean = hourMinute == messageVO.hourMinute

    fun isEqualDate(messageVO: MessageVO): Boolean = date == messageVO.date
}

fun Message.mapToPresenter() = MessageVO(
    uid = uid,
    date = TimeConverter.lidToYearMonthDay(lid),
    hourMinute = TimeConverter.lidToHourMinute(lid),
    message = message,
    transactionId = tid,
    lid = lid
)

fun ChatMessage.mapToPresenter() = MessageVO(
    uid = uid,
    date = TimeConverter.lidToYearMonthDay(lid),
    hourMinute = TimeConverter.lidToHourMinute(lid),
    message = message,
    transactionId = tid,
    lid = lid
)

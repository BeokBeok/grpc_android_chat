package com.example.grpc_android.talk.vo

import com.example.grpc_android.data.entity.MessageEntity
import com.example.grpc_android.util.TimeConverter
import io.grpc.chat.Message

data class MessageVO(
    val uid: String = "",
    val date: String = "",
    val hourMinute: String = "",
    val message: String = "",
    val transactionId: String = "",
    var isShowProfile: Boolean = true,
    var isShowHourMinute: Boolean = true
) {

    fun isEqualUid(messageVO: MessageVO): Boolean = uid == messageVO.uid

    fun isEqualHourMinute(messageVO: MessageVO): Boolean = hourMinute == messageVO.hourMinute

    fun isEqualDate(messageVO: MessageVO): Boolean = date == messageVO.date
}

fun Message.mapToPresenter() = MessageVO(
    uid = uid,
    date = TimeConverter.lidToYearMonthDay(this.lid),
    hourMinute = TimeConverter.lidToHourMinute(this.lid),
    message = message,
    transactionId = tid
)

fun MessageEntity.mapToPresenter() = MessageVO(
    uid = uid,
    date = TimeConverter.lidToYearMonthDay(this.lid),
    hourMinute = TimeConverter.lidToHourMinute(this.lid),
    message = message,
    transactionId = tid
)

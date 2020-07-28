package com.example.grpc_android.talk.model

import com.example.grpc_android.util.TimeConverter
import io.grpc.chat.Message

data class MessageData(
    val date: String = "",
    val hourMinute: String = "",
    val message: String = "",
    val transactionId: String = "",
    val userId: String = "",
    var isShowProfile: Boolean = true,
    var isShowHourMinute: Boolean = true
)

fun Message.mapToPresenter() = MessageData(
    date = TimeConverter.lidToYearMonthDay(this.lid),
    hourMinute = TimeConverter.lidToHourMinute(this.lid),
    message = this.message,
    transactionId = this.tid,
    userId = this.uid
)
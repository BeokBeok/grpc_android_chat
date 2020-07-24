package com.example.grpc_android.talk.model

import com.example.grpc_android.util.TimeConverter
import io.grpc.chat.Message

data class MessageData(
    val time: String,
    val message: String,
    val transactionId: String,
    val userId: String
)

fun Message.mapToPresenter() = MessageData(
    time = TimeConverter.lidToTime(this.lid),
    message = this.message,
    transactionId = this.tid,
    userId = this.uid
)
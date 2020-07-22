package com.example.grpc_android.talk.model

import io.grpc.chat.Message

data class MessageData(
    val lastChatId: String,
    val message: String,
    val transactionId: String,
    val userId: String
)

fun Message.mapToPresenter() = MessageData(
    lastChatId = this.lid,
    message = this.message,
    transactionId = this.tid,
    userId = this.uid
)
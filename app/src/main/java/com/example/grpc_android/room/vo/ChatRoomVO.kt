package com.example.grpc_android.room.vo

import com.example.grpc_android.data.entity.ChatRoom

data class ChatRoomVO(val chatId: String)

fun ChatRoom.mapToPresenter() = ChatRoomVO(chatId = this.chatId)

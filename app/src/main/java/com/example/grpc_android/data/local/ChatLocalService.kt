package com.example.grpc_android.data.local

import com.example.grpc_android.data.local.entity.ChatRoom

interface ChatLocalService {

    suspend fun saveChatRoom(vararg chatRoom: ChatRoom)

    suspend fun getChatRooms(): List<ChatRoom>
}
package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatRoom

interface ChatLocalService {

    suspend fun saveChatRoom(chatRoom: ChatRoom)

    suspend fun updateChatRoom(chatRoom: ChatRoom)

    suspend fun getChatRooms(): List<ChatRoom>

    suspend fun deleteChatRoomByCid(cid: String)
}

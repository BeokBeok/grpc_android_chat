package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse

interface ChatLocalService {

    suspend fun updateChatRoom(syncChatsResponse: SyncChatsResponse)

    suspend fun getChatRooms(): List<ChatRoom>
}

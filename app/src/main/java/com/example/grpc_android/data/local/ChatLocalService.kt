package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse

interface ChatLocalService {

    suspend fun updateChatRoom(syncChatsResponse: SyncChatsResponse)

    suspend fun getChatRooms(): List<ChatRoom>

    suspend fun getChatMessages(cid: String): List<ChatMessage>

    suspend fun getLastSyncLid(cid: String): String

    suspend fun updateChatMessage(chatMessages: List<ChatMessage>)
}

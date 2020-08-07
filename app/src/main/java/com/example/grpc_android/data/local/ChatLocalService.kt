package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse
import io.grpc.chat.SyncLogsResponse

interface ChatLocalService {

    suspend fun updateChatRoom(syncChatsResponse: SyncChatsResponse)

    suspend fun getChatRooms(): List<ChatRoom>

    suspend fun updateChatMessage(chatId: String, syncLogsResponse: SyncLogsResponse)

    suspend fun getChatMessage(chatId: String): ChatMessage

    suspend fun getLastSyncLid(chatId: String): String
}

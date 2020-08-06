package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.entity.ChatMessage
import io.grpc.chat.SyncLogsResponse

@Dao
interface ChatMessageDao : BaseDao<ChatMessage> {

    @Query("SELECT * FROM ChatMessage WHERE cid = :cid")
    suspend fun getByChatId(cid: String): ChatMessage?

    @Transaction
    suspend fun updateChatMessages(chatId: String, syncLogsResponse: SyncLogsResponse) {
        val cachedMessages = getByChatId(chatId)?.messages ?: emptyList()
        val addedMessages = syncLogsResponse.messagesList.map { it.message }
        val updatedMessages = cachedMessages.toMutableList().apply { addAll(addedMessages) }

        if (cachedMessages.isEmpty()) {
            insert(ChatMessage(chatId = chatId, messages = updatedMessages))
            return
        }
        update(ChatMessage(chatId = chatId, messages = updatedMessages))
    }
}

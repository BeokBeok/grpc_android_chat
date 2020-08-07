package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse
import io.grpc.chat.SyncLogsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val chatRoomDao: ChatRoomDao,
    private val chatMessageDao: ChatMessageDao
) : ChatLocalService {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun updateChatRoom(syncChatsResponse: SyncChatsResponse) =
        withContext(ioDispatcher) {
            chatRoomDao.updateChatRoomList(syncChatsResponse)
        }

    override suspend fun getChatRooms(): List<ChatRoom> = withContext(ioDispatcher) {
        chatRoomDao.get()
    }

    override suspend fun updateChatMessage(chatId: String, syncLogsResponse: SyncLogsResponse) =
        withContext(ioDispatcher) {
            chatMessageDao.updateChatMessages(chatId, syncLogsResponse)
        }

    override suspend fun getChatMessage(chatId: String): ChatMessage = withContext(ioDispatcher) {
        chatMessageDao.getByChatId(chatId) ?: ChatMessage(
            chatId = "",
            messages = listOf(),
            lastSyncLid = ""
        )
    }
}

package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse
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

    override suspend fun getChatMessages(cid: String): List<ChatMessage> =
        withContext(ioDispatcher) {
            chatMessageDao.getByCid(cid)
        }

    override suspend fun getLastSyncLid(cid: String): String = withContext(ioDispatcher) {
        chatRoomDao.getLastSyncLidByCid(cid)
    }

    override suspend fun updateChatMessage(chatMessages: List<ChatMessage>) {
        withContext(ioDispatcher) {
            chatMessages.map { chatMessageDao.insert(it) }
        }
    }
}

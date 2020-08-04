package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val chatRoomDao: ChatRoomDao
) : ChatLocalService {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun updateChatRoom(syncChatsResponse: SyncChatsResponse) =
        withContext(ioDispatcher) {
            chatRoomDao.updateChatRoomList(syncChatsResponse)
        }

    override suspend fun getChatRooms(): List<ChatRoom> = withContext(ioDispatcher) {
        chatRoomDao.get()
    }
}

package com.example.grpc_android.data.local

import com.example.grpc_android.data.entity.ChatRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val chatRoomDao: ChatRoomDao
) : ChatLocalService {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun saveChatRoom(chatRoom: ChatRoom) = withContext(ioDispatcher) {
        chatRoomDao.insert(chatRoom)
    }

    override suspend fun updateChatRoom(chatRoom: ChatRoom) = withContext(ioDispatcher) {
        chatRoomDao.update(chatRoom)
    }

    override suspend fun getChatRooms(): List<ChatRoom> = withContext(ioDispatcher) {
        chatRoomDao.get()
    }

    override suspend fun deleteChatRoomByCid(cid: String) = withContext(ioDispatcher) {
        chatRoomDao.deleteByChatId(cid)
    }
}

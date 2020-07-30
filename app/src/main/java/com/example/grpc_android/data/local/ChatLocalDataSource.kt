package com.example.grpc_android.data.local

import com.example.grpc_android.data.local.entity.ChatRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val chatRoomDao: ChatRoomDao
) : ChatLocalService {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun saveChatRoom(vararg chatRoom: ChatRoom) = withContext(ioDispatcher) {
        chatRoomDao.insert(*chatRoom)
    }

    override suspend fun getChatRooms(): List<ChatRoom> = withContext(ioDispatcher) {
        chatRoomDao.getChatRoom()
    }
}
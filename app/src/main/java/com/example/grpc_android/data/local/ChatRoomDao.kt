package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse

@Dao
interface ChatRoomDao : BaseDao<ChatRoom> {

    @Query("SELECT * FROM Chats ORDER BY cid")
    suspend fun get(): List<ChatRoom>

    @Query("SELECT lastSyncLid FROM Chats WHERE cid = :cid")
    suspend fun getLastSyncLidByCid(cid: String): String

    @Transaction
    suspend fun updateChatRoomList(syncChatsResponse: SyncChatsResponse) {
        syncChatsResponse.run {
            delCidsList
                .also { if (it.isEmpty()) return@also }
                .map { ChatRoom(chatId = it) }
                .map { delete(it) }
            updCidsList
                .also { if (it.isEmpty()) return@also }
                .map { ChatRoom(chatId = it) }
                .map { insert(it) }
        }
    }
}

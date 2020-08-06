package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.SyncChatsResponse

@Dao
interface ChatRoomDao : BaseDao<ChatRoom> {

    @Query("SELECT * FROM ChatRoom ORDER BY cid")
    suspend fun get(): List<ChatRoom>

    @Transaction
    suspend fun updateChatRoomList(syncChatsResponse: SyncChatsResponse) {
        syncChatsResponse.run {
            delCidsList
                .also { if (it.isEmpty()) return@also }
                .map(::ChatRoom)
                .map { delete(it) }
            updCidsList
                .also { if (it.isEmpty()) return@also }
                .map(::ChatRoom)
                .map { insert(it) }
        }
    }
}

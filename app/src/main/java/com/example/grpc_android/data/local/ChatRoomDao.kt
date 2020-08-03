package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.entity.ChatRoom

@Dao
interface ChatRoomDao : BaseDao<ChatRoom> {

    @Query("SELECT * FROM ChatRoom ORDER BY cid")
    suspend fun get(): List<ChatRoom>

    @Query("DELETE FROM ChatRoom WHERE cid = :cid")
    suspend fun deleteByChatId(cid: String)
}

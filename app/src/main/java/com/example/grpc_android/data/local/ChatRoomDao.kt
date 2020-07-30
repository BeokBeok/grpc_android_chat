package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.local.entity.ChatRoom

@Dao
interface ChatRoomDao : BaseDao<ChatRoom> {

    @Query("SELECT * FROM ChatRoom")
    suspend fun getChatRoom(): List<ChatRoom>
}
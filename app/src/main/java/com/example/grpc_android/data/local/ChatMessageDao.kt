package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.grpc_android.base.BaseDao
import com.example.grpc_android.data.entity.ChatMessage

@Dao
interface ChatMessageDao : BaseDao<ChatMessage> {

    @Query("SELECT * FROM ChatLogs WHERE cid = :cid ORDER BY lid")
    suspend fun getByCid(cid: String): List<ChatMessage>
}

package com.example.grpc_android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grpc_android.data.local.entity.ChatRoom

@Dao
interface ChatRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoom)

    @Query("SELECT * FROM ChatRoom")
    suspend fun getChatRoom(): List<ChatRoom>
}
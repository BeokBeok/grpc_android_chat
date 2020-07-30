package com.example.grpc_android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grpc_android.data.local.entity.ChatRoom

@Database(
    entities = [ChatRoom::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatRoomDao(): ChatRoomDao
}
package com.example.grpc_android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import com.example.grpc_android.util.Converter

@Database(
    entities = [ChatRoom::class, ChatMessage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatRoomDao(): ChatRoomDao

    abstract fun chatMessageDao(): ChatMessageDao
}

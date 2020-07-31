package com.example.grpc_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatRoom")
data class ChatRoom(
    @PrimaryKey @ColumnInfo(name = "cid") val chatId: String
)
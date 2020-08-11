package com.example.grpc_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Chats")
data class ChatRoom(
    @PrimaryKey @ColumnInfo(name = "cid")
    val chatId: String,

    @ColumnInfo(name = "lastSyncLid")
    val lastSyncLid: String = ""
)

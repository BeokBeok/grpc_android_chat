package com.example.grpc_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ChatMessage",
    foreignKeys = [
        ForeignKey(
            entity = ChatRoom::class,
            parentColumns = arrayOf("cid"),
            childColumns = arrayOf("cid"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatMessage(
    @PrimaryKey @ColumnInfo(name = "cid") val chatId: String,

    @ColumnInfo(name = "messages")
    val messages: List<MessageEntity>,

    @ColumnInfo(name = "lastSyncLid")
    val lastSyncLid: String
)

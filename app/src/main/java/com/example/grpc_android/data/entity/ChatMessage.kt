package com.example.grpc_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import io.grpc.chat.Message

@Entity(
    tableName = "ChatLogs",
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
    @PrimaryKey
    @ColumnInfo(name = "lid")
    val lid: String,

    @ColumnInfo(name = "cid")
    val cid: String,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "prevLid")
    val prevLid: String,

    @ColumnInfo(name = "tid")
    val tid: String,

    @ColumnInfo(name = "message")
    val message: String
)

fun Message.mapToEntity(cid: String) = ChatMessage(
    lid = lid,
    cid = cid,
    uid = uid,
    prevLid = prevLid,
    tid = tid,
    message = message
)

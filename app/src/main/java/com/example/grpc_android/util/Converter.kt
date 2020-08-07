package com.example.grpc_android.util

import androidx.room.TypeConverter
import com.example.grpc_android.data.entity.MessageEntity
import com.squareup.moshi.Moshi

class Converter {

    private val moshi = Moshi.Builder().build()
    private val messageJsonAdapter = moshi.adapter<MessageEntity>(MessageEntity::class.java)

    @TypeConverter
    fun fromListOfMessage(listOfMessage: List<MessageEntity>): String =
        listOfMessage.joinToString { messageJsonAdapter.toJson(it) }

    @TypeConverter
    fun toListOfMessage(flatMessageList: String): List<MessageEntity> =
        flatMessageList.split(", ").map { messageJsonAdapter.fromJson(it) ?: MessageEntity() }
}

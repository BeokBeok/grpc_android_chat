package com.example.grpc_android.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.grpc.chat.Message

@JsonClass(generateAdapter = true)
data class MessageEntity(

    @Json(name = "uid")
    val uid: String = "",

    @Json(name = "tid")
    val tid: String = "",

    @Json(name = "lid")
    val lid: String = "",

    @Json(name = "prevLid")
    val prevLid: String = "",

    @Json(name = "message")
    val message: String = ""
)

fun Message.mapToEntity() = MessageEntity(
    uid = uid,
    tid = tid,
    lid = lid,
    prevLid = prevLid,
    message = message
)

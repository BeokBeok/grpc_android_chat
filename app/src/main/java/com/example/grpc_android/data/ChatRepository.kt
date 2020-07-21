package com.example.grpc_android.data

import io.grpc.chat.Receive
import io.grpc.chat.ResponseWithError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    // Remote
    fun eventListen(uid: String): Flow<Receive>

    suspend fun chatWith(uid: String, peerName: String): Result<ResponseWithError>

    suspend fun sendMessage(uid: String, cid: String, msg: String): Result<ResponseWithError>

    suspend fun chatIn(uid: String, cid: String): Result<ResponseWithError>

    suspend fun chatOut(uid: String, cid: String): Result<ResponseWithError>

    suspend fun getMessages(cid: String): Result<ResponseWithError>
}
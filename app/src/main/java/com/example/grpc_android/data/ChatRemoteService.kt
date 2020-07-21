package com.example.grpc_android.data

import io.grpc.chat.Receive
import io.grpc.chat.Request
import io.grpc.chat.ResponseWithError
import kotlinx.coroutines.flow.Flow

interface ChatRemoteService {

    fun eventListen(request: Request): Flow<Receive>

    suspend fun chatWith(request: Request): ResponseWithError

    suspend fun sendMessage(request: Request): ResponseWithError

    suspend fun getUsers(request: Request): ResponseWithError

    suspend fun getRooms(request: Request): ResponseWithError

    suspend fun chatIn(request: Request): ResponseWithError

    suspend fun chatOut(request: Request): ResponseWithError

    suspend fun getMessages(request: Request): ResponseWithError

    suspend fun receiveAck(request: Request): ResponseWithError

    suspend fun readAck(request: Request): ResponseWithError

    suspend fun syncChats(request: Request): ResponseWithError
}
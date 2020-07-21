package com.example.grpc_android.data

import io.grpc.chat.ChatGrpcKt
import io.grpc.chat.Receive
import io.grpc.chat.Request
import io.grpc.chat.ResponseWithError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val chatService: ChatGrpcKt.ChatCoroutineStub
) : ChatRemoteService {

    override fun eventListen(request: Request): Flow<Receive> =
        chatService.eventListen(request)

    override suspend fun chatWith(request: Request): ResponseWithError =
        chatService.chatWith(request)

    override suspend fun sendMessage(request: Request): ResponseWithError =
        chatService.sendMessage(request)

    override suspend fun getUsers(request: Request): ResponseWithError =
        chatService.getUsers(request)

    override suspend fun getRooms(request: Request): ResponseWithError =
        chatService.getRooms(request)

    override suspend fun chatIn(request: Request): ResponseWithError =
        chatService.chatIn(request)

    override suspend fun chatOut(request: Request): ResponseWithError =
        chatService.chatOut(request)

    override suspend fun getMessages(request: Request): ResponseWithError =
        chatService.getMessages(request)

    override suspend fun receiveAck(request: Request): ResponseWithError =
        chatService.receiveAck(request)

    override suspend fun readAck(request: Request): ResponseWithError =
        chatService.readAck(request)

    override suspend fun syncChats(request: Request): ResponseWithError =
        chatService.syncChats(request)
}
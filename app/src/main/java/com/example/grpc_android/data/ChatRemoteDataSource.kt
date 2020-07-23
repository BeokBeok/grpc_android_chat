package com.example.grpc_android.data

import io.grpc.chat.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val chatService: ChatGrpcKt.ChatCoroutineStub
) : ChatRemoteService {

    override fun eventListen(request: EventListenRequest): Flow<Receive> =
        chatService.eventListen(request)

    override suspend fun chatWith(request: CreateRequest): CreateResponse =
        chatService.create(request)

    override suspend fun sendMessage(request: WriteRequest): WriteResponse =
        chatService.write(request)

    override suspend fun getUsers(request: GetUsersRequest): GetUsersResponse =
        chatService.getUsers(request)

    override suspend fun getRooms(request: GetRoomsRequest): GetRoomsResponse =
        chatService.getRooms(request)

    override suspend fun chatIn(request: ChatInRequest): ChatInResponse =
        chatService.chatIn(request)

    override suspend fun chatOut(request: ChatOutRequest): ChatOutResponse =
        chatService.chatOut(request)

    override suspend fun getMessages(request: GetMessagesRequest): GetMessagesResponse =
        chatService.getMessages(request)

    override suspend fun receiveAck(request: ReceiveAckRequest): ReceiveAckResponse =
        chatService.receiveAck(request)

    override suspend fun readAck(request: ReadAckRequest): ReadAckResponse =
        chatService.readAck(request)

    override suspend fun syncChats(request: SyncChatsRequest): SyncChatsResponse =
        chatService.syncChats(request)
}
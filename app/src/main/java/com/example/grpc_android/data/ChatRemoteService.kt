package com.example.grpc_android.data

import io.grpc.chat.*
import kotlinx.coroutines.flow.Flow

interface ChatRemoteService {

    fun eventListen(request: EventListenRequest): Flow<Receive>

    suspend fun chatWith(request: CreateRequest): CreateResponse

    suspend fun sendMessage(request: WriteRequest): WriteResponse

    suspend fun getUsers(request: GetUsersRequest): GetUsersResponse

    suspend fun getRooms(request: GetRoomsRequest): GetRoomsResponse

    suspend fun chatIn(request: ChatInRequest): ChatInResponse

    suspend fun chatOut(request: ChatOutRequest): ChatOutResponse

    suspend fun getMessages(request: GetMessagesRequest): GetMessagesResponse

    suspend fun receiveAck(request: ReceiveAckRequest): ReceiveAckResponse

    suspend fun readAck(request: ReadAckRequest): ReadAckResponse

    suspend fun syncChats(request: SyncChatsRequest): SyncChatsResponse
}
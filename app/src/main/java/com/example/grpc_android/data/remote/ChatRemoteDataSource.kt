package com.example.grpc_android.data.remote

import io.grpc.Metadata
import io.grpc.chat.*
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    chatService: ChatGrpcKt.ChatCoroutineStub
) : ChatRemoteService {

    private val headerAttachedService = MetadataUtils.attachHeaders(
        chatService,
        Metadata().apply {
            put(
                Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjMmMuY29uZWN0cy5jb20iLCJleHAiOjE2MDE5NTkwNzksImp0aSI6Ikd5dVI4c1ZiTktNRng4Sm5Xb0RqdUYiLCJpYXQiOjE2MDE4NzI2NzksImlzcyI6IkNvbmVjdHMgQXV0aCIsInN1YiI6IkNvbmVjdHMgU2VydmljZSBUb2tlbiIsImlkeCI6MCwib3duZXIiOiJuZXdybyJ9.HMfBnG-s88X2cw62CwXj9Uc13LTJRRwCQ2c7IePC3tk"
            )
        }
    )

    override fun eventListen(request: EventListenRequest): Flow<Receive> =
        headerAttachedService.eventListen(request)

    override suspend fun chatWith(request: CreateRequest): CreateResponse =
        headerAttachedService.create(request)

    override suspend fun sendMessage(request: WriteRequest): WriteResponse =
        headerAttachedService.write(request)

    override suspend fun getUsers(request: GetUsersRequest): GetUsersResponse =
        headerAttachedService.getUsers(request)

    override suspend fun getRooms(request: GetRoomsRequest): GetRoomsResponse =
        headerAttachedService.getRooms(request)

    override suspend fun chatIn(request: ChatInRequest): ChatInResponse =
        headerAttachedService.chatIn(request)

    override suspend fun chatOut(request: ChatOutRequest): ChatOutResponse =
        headerAttachedService.chatOut(request)

    override suspend fun getMessages(request: GetMessagesRequest): GetMessagesResponse =
        headerAttachedService.getMessages(request)

    override suspend fun receiveAck(request: ReceiveAckRequest): ReceiveAckResponse =
        headerAttachedService.receiveAck(request)

    override suspend fun readAck(request: ReadAckRequest): ReadAckResponse =
        headerAttachedService.readAck(request)

    override suspend fun syncChats(request: SyncChatsRequest): SyncChatsResponse =
        headerAttachedService.syncChats(request)

    override suspend fun syncLogs(request: SyncLogsRequest): SyncLogsResponse =
        headerAttachedService.syncLogs(request)

    override suspend fun join(request: JoinRequest): JoinResponse =
        headerAttachedService.join(request)
}

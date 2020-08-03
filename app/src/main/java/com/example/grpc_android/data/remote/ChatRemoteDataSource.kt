package com.example.grpc_android.data.remote

import io.grpc.Metadata
import io.grpc.chat.*
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val chatService: ChatGrpcKt.ChatCoroutineStub
) : ChatRemoteService {

    override fun eventListen(request: EventListenRequest): Flow<Receive> =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).eventListen(request)

    override suspend fun chatWith(request: CreateRequest): CreateResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).create(request)

    override suspend fun sendMessage(request: WriteRequest): WriteResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).write(request)

    override suspend fun getUsers(request: GetUsersRequest): GetUsersResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).getUsers(request)

    override suspend fun getRooms(request: GetRoomsRequest): GetRoomsResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).getRooms(request)

    override suspend fun chatIn(request: ChatInRequest): ChatInResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).chatIn(request)

    override suspend fun chatOut(request: ChatOutRequest): ChatOutResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).chatOut(request)

    override suspend fun getMessages(request: GetMessagesRequest): GetMessagesResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).getMessages(request)

    override suspend fun receiveAck(request: ReceiveAckRequest): ReceiveAckResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).receiveAck(request)

    override suspend fun readAck(request: ReadAckRequest): ReadAckResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).readAck(request)

    override suspend fun syncChats(request: SyncChatsRequest): SyncChatsResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).syncChats(request)

    override suspend fun syncLogs(request: SyncLogsRequest): SyncLogsResponse =
        MetadataUtils.attachHeaders(
            chatService,
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer ${request.meta.uid}"
                )
            }
        ).syncLogs(request)
}

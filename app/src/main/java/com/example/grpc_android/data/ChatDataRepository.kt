package com.example.grpc_android.data

import com.google.protobuf.Timestamp
import io.grpc.chat.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatDataRepository @Inject constructor(
    private val chatRemoteDataSource: ChatRemoteDataSource
) : ChatRepository {

    private val ioDispatcher = Dispatchers.IO

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

    override fun eventListen(uid: String): Flow<Receive> = flow<Receive> {
        val payloadModel = Payload.newBuilder().apply {
            stream = OpenStream.newBuilder().apply {
                name = uid
            }.build()
        }.build()

        val request = Request.newBuilder().apply {
            timestamp = Timestamp.getDefaultInstance()
            payload = payloadModel
        }.build()

        chatRemoteDataSource.eventListen(request)
    }.flowOn(ioDispatcher + coroutineExceptionHandler)

    override suspend fun chatWith(uid: String, peerName: String): Result<ResponseWithError> =
        withContext(ioDispatcher + coroutineExceptionHandler) {
            val chatWithModel = ChatWith.newBuilder().apply {
                name = uid
                peer = peerName
            }.build()

            val payloadModel = Payload.newBuilder().apply {
                chatWith = chatWithModel
            }.build()

            val request = Request.newBuilder().apply {
                pldType = PayloadType.CHATWITH
                payload = payloadModel
            }.build()

            runCatching { chatRemoteDataSource.chatWith(request) }
        }

    override suspend fun sendMessage(
        uid: String,
        cid: String,
        msg: String
    ): Result<ResponseWithError> =
        withContext(ioDispatcher + coroutineExceptionHandler) {
            val sendMessageModel = Message.newBuilder().apply {
                this.uid = uid
                message = msg
            }.build()

            val payloadModel = Payload.newBuilder().apply {
                message = sendMessageModel
            }.build()

            val request = Request.newBuilder().apply {
                this.cid = cid
                pldType = PayloadType.MESSAGE
                payload = payloadModel
            }.build()

            runCatching { chatRemoteDataSource.sendMessage(request) }
        }

    override suspend fun chatIn(uid: String, cid: String): Result<ResponseWithError> =
        withContext(ioDispatcher + coroutineExceptionHandler) {
            val chatInModel = ChatIn.newBuilder().apply {
                this.uid = uid
                chatPublicChecksum = "00000000000000000"
                chatInChecksum = "00000000000000000"
            }.build()

            val payloadModel = Payload.newBuilder().apply {
                chatIn = chatInModel
            }.build()

            val request = Request.newBuilder().apply {
                this.cid = cid
                pldType = PayloadType.CHATIN
                payload = payloadModel
            }.build()

            runCatching { chatRemoteDataSource.chatIn(request) }
        }

    override suspend fun chatOut(uid: String, cid: String): Result<ResponseWithError> =
        withContext(ioDispatcher + coroutineExceptionHandler) {
            val chatOutModel = ChatOut.newBuilder().apply {
                this.uid = uid
                lastMsgLid = "0"
            }.build()

            val payloadModel = Payload.newBuilder().apply {
                chatOut = chatOutModel
            }.build()

            val request = Request.newBuilder().apply {
                this.cid = cid
                pldType = PayloadType.CHATOUT
                payload = payloadModel
            }.build()

            runCatching { chatRemoteDataSource.chatOut(request) }
        }

    override suspend fun getMessages(cid: String): Result<ResponseWithError> =
        withContext(ioDispatcher + coroutineExceptionHandler) {
            val paginationModel = Pagination.newBuilder().apply {
                pageSize = 30
                pageToken = 1
            }.build()

            val getMessagesModel = Messages.newBuilder().apply {
                lastLid = "0"
                pagination = paginationModel
            }.build()

            val payloadModel = Payload.newBuilder().apply {
                messages = getMessagesModel
            }.build()

            val request = Request.newBuilder().apply {
                this.cid = cid
                pldType = PayloadType.GETMESSAGES
                payload = payloadModel
            }.build()

            runCatching { chatRemoteDataSource.getMessages(request) }
        }
}
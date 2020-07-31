package com.example.grpc_android.data

import com.example.grpc_android.data.entity.ChatRoom
import com.example.grpc_android.data.local.ChatLocalDataSource
import com.example.grpc_android.data.remote.ChatRemoteDataSource
import com.google.protobuf.Timestamp
import io.grpc.chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatDataRepository @Inject constructor(
    private val chatLocalDataSource: ChatLocalDataSource,
    private val chatRemoteDataSource: ChatRemoteDataSource
) : ChatRepository {

    private val ioDispatcher = Dispatchers.IO

    override fun eventListen(uid: String): Flow<Receive> {
        val request = EventListenRequest.newBuilder().apply {
            meta = Meta.newBuilder().apply {
                this.uid = uid
                timestamp = Timestamp.getDefaultInstance()
            }.build()
        }.build()

        return chatRemoteDataSource.eventListen(request).flowOn(ioDispatcher)
    }

    override suspend fun chatWith(uid: String, peerName: String): Result<CreateResponse> =
        withContext(ioDispatcher) {
            val request = CreateRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    this.uid = uid
                    peer = peerName
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.chatWith(request) }
        }

    override suspend fun sendMessage(uid: String, cid: String, msg: String): Result<WriteResponse> =
        withContext(ioDispatcher) {
            val request = WriteRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    this.uid = uid
                    this.cid = cid
                    message = msg
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.sendMessage(request) }
        }

    override suspend fun chatIn(uid: String, cid: String): Result<ChatInResponse> =
        withContext(ioDispatcher) {
            val request = ChatInRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    this.uid = uid
                    this.cid = cid
                    chatPublicChecksum = "00000000000000000"
                    chatInChecksum = "00000000000000000"
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.chatIn(request) }
        }

    override suspend fun chatOut(uid: String, cid: String): Result<ChatOutResponse> =
        withContext(ioDispatcher) {
            val request = ChatOutRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    this.uid = uid
                    this.cid = cid
                    lastMsgLid = "0"
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.chatOut(request) }
        }

    override suspend fun getMessages(uid: String, cid: String): Result<GetMessagesResponse> =
        withContext(ioDispatcher) {
            val request = GetMessagesRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    lastLid = "0"
                    this.uid = uid
                    this.cid = cid
                }.build()
                pagination = Pagination.newBuilder().apply {
                    pageSize = 1_000
                    pageToken = 1
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.getMessages(request) }
        }

    override suspend fun getRooms(uid: String): Result<GetRoomsResponse> =
        withContext(ioDispatcher) {
            val request = GetRoomsRequest.newBuilder().apply {
                meta = Meta.newBuilder().apply {
                    this.uid = uid
                }.build()
            }.build()

            runCatching { chatRemoteDataSource.getRooms(request) }
        }

    override suspend fun syncChats(): Result<List<ChatRoom>> = withContext(ioDispatcher) {
        val cachedChatRooms = async { chatLocalDataSource.getChatRooms() }
        val requestChatRooms = async {
            val request = SyncChatsRequest.newBuilder().apply {
                lastCid = "0"
                syncChatChecksum = "0"
                fetchCount = SYNC_CHAT_FETCH_COUNT
            }.build()
            chatRemoteDataSource.syncChats(request)
        }

        cachedChatRooms.await().let {
            var chatRoomsResponse = requestChatRooms.await()
            if (it.isEmpty()) {
                while (!chatRoomsResponse.eof) {
                    chatLocalDataSource.saveChatRoom(
                        *chatRoomsResponse.updCidsList.map(::ChatRoom).toTypedArray()
                    )
                    val request = SyncChatsRequest.newBuilder().apply {
                        lastCid = chatRoomsResponse.lastCid
                        syncChatChecksum = chatRoomsResponse.syncChatChecksum
                        fetchCount = SYNC_CHAT_FETCH_COUNT
                    }.build()
                    chatRoomsResponse = chatRemoteDataSource.syncChats(request)
                }
                return@withContext runCatching { chatLocalDataSource.getChatRooms() }
            }
            while (!chatRoomsResponse.eof) {
                val (cidGroupToBeDeleted, cidGroupToBeUpdated) = chatRoomsResponse.delCidsList to chatRoomsResponse.updCidsList
                if (cidGroupToBeDeleted.isNotEmpty()) {
                    cidGroupToBeDeleted.forEach { cid ->
                        chatLocalDataSource.deleteChatRoomByCid(cid)
                    }
                }
                chatLocalDataSource.saveChatRoom(
                    *cidGroupToBeUpdated.map(::ChatRoom).toTypedArray()
                )
                val request = SyncChatsRequest.newBuilder().apply {
                    lastCid = chatRoomsResponse.lastCid
                    syncChatChecksum = chatRoomsResponse.syncChatChecksum
                    fetchCount = SYNC_CHAT_FETCH_COUNT
                }.build()
                chatRoomsResponse = chatRemoteDataSource.syncChats(request)
            }
        }

        return@withContext runCatching { chatLocalDataSource.getChatRooms() }
    }

    companion object {
        private const val SYNC_CHAT_FETCH_COUNT = 30
    }
}
package com.example.grpc_android.data

import com.example.grpc_android.data.entity.ChatRoom
import com.example.grpc_android.data.local.ChatLocalDataSource
import com.example.grpc_android.data.remote.ChatRemoteDataSource
import com.example.grpc_android.util.Prefs
import com.google.protobuf.Timestamp
import io.grpc.chat.*
import kotlinx.coroutines.Dispatchers
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
        val meta = Meta.newBuilder()
            .setUid(uid)
            .setTimestamp(Timestamp.getDefaultInstance())
            .build()
        val request = EventListenRequest.newBuilder()
            .setMeta(meta)
            .build()

        return chatRemoteDataSource.eventListen(request).flowOn(ioDispatcher)
    }

    override suspend fun chatWith(uid: String, peerName: String): Result<CreateResponse> =
        withContext(ioDispatcher) {
            val meta = Meta.newBuilder()
                .setUid(uid)
                .build()
            val request = CreateRequest.newBuilder()
                .setMeta(meta)
                .build()

            runCatching { chatRemoteDataSource.chatWith(request) }
        }

    override suspend fun sendMessage(uid: String, cid: String, msg: String): Result<WriteResponse> =
        withContext(ioDispatcher) {
            val meta = Meta.newBuilder()
                .setUid(uid)
                .setCid(cid)
                .build()
            val request = WriteRequest.newBuilder()
                .setMeta(meta)
                .setMessage(msg)
                .build()

            runCatching { chatRemoteDataSource.sendMessage(request) }
        }

    override suspend fun chatIn(uid: String, cid: String): Result<ChatInResponse> =
        withContext(ioDispatcher) {
            val meta = Meta.newBuilder()
                .setUid(uid)
                .setCid(cid)
                .build()
            val request = ChatInRequest.newBuilder()
                .setMeta(meta)
                .setChatPublicChecksum("00000000000000000")
                .setChatInChecksum("00000000000000000")
                .build()

            runCatching { chatRemoteDataSource.chatIn(request) }
        }

    override suspend fun chatOut(uid: String, cid: String): Result<ChatOutResponse> =
        withContext(ioDispatcher) {
            val meta = Meta.newBuilder()
                .setUid(uid)
                .setCid(cid)
                .build()
            val request = ChatOutRequest.newBuilder()
                .setMeta(meta)
                .setLastMsgLid("0")
                .build()

            runCatching { chatRemoteDataSource.chatOut(request) }
        }

    override suspend fun getMessages(uid: String, cid: String): Result<GetMessagesResponse> =
        withContext(ioDispatcher) {
            val meta = Meta.newBuilder()
                .setUid(uid)
                .setCid(cid)
                .build()
            val pagination = Pagination.newBuilder().apply {
                pageSize = 1_000
                pageToken = 1
            }.build()
            val request = GetMessagesRequest.newBuilder()
                .setMeta(meta)
                .setLastLid("0")
                .setPagination(pagination)
                .build()

            runCatching { chatRemoteDataSource.getMessages(request) }
        }

    override suspend fun getRooms(uid: String): Result<List<ChatRoom>> =
        withContext(ioDispatcher) {
            runCatching { chatLocalDataSource.getChatRooms() }
        }

    override suspend fun syncChats(uid: String): Result<List<ChatRoom>> =
        withContext(ioDispatcher) {
            runCatching {
                val cachedChatRooms = chatLocalDataSource.getChatRooms()
                if (cachedChatRooms.isEmpty()) {
                    val meta = Meta.newBuilder()
                        .setUid(uid)
                        .build()
                    var request = SyncChatsRequest.newBuilder()
                        .setMeta(meta)
                        .setLastCid(Prefs.lastCid)
                        .setSyncChatChecksum(Prefs.syncChatChecksum)
                        .setFetchCount(SYNC_CHAT_FETCH_COUNT)
                        .build()
                    var fetchedChatRooms = chatRemoteDataSource.syncChats(request)
                    if (fetchedChatRooms.updCidsCount == 0) return@runCatching emptyList<ChatRoom>()

                    while (!fetchedChatRooms.eof) {
                        chatLocalDataSource.saveChatRoom(
                            *fetchedChatRooms.updCidsList.map(::ChatRoom).toTypedArray()
                        )
                        Prefs.lastCid = fetchedChatRooms.lastCid
                        Prefs.syncChatChecksum = fetchedChatRooms.syncChatChecksum
                        request = SyncChatsRequest.newBuilder()
                            .setMeta(meta)
                            .setLastCid(Prefs.lastCid)
                            .setSyncChatChecksum(Prefs.syncChatChecksum)
                            .setFetchCount(SYNC_CHAT_FETCH_COUNT)
                            .build()
                        fetchedChatRooms = chatRemoteDataSource.syncChats(request)
                    }
                    chatLocalDataSource.saveChatRoom(
                        *fetchedChatRooms.updCidsList.map(::ChatRoom).toTypedArray()
                    )
                    Prefs.lastCid = fetchedChatRooms.lastCid
                    Prefs.syncChatChecksum = fetchedChatRooms.syncChatChecksum
                    return@runCatching chatLocalDataSource.getChatRooms()
                }
                val meta = Meta.newBuilder()
                    .setUid(uid)
                    .build()
                var request = SyncChatsRequest.newBuilder()
                    .setMeta(meta)
                    .setLastCid(Prefs.lastCid)
                    .setSyncChatChecksum(Prefs.syncChatChecksum)
                    .setFetchCount(SYNC_CHAT_FETCH_COUNT)
                    .build()
                var fetchedChatRooms = chatRemoteDataSource.syncChats(request)
                Prefs.lastCid = fetchedChatRooms.lastCid
                Prefs.syncChatChecksum = fetchedChatRooms.syncChatChecksum

                while (!fetchedChatRooms.eof) {
                    fetchedChatRooms.delCidsList.map { chatLocalDataSource.deleteChatRoomByCid(it) }
                    chatLocalDataSource.saveChatRoom(
                        *fetchedChatRooms.updCidsList.map(::ChatRoom).toTypedArray()
                    )
                    Prefs.lastCid = fetchedChatRooms.lastCid
                    Prefs.syncChatChecksum = fetchedChatRooms.syncChatChecksum
                    request = SyncChatsRequest.newBuilder()
                        .setMeta(meta)
                        .setLastCid(Prefs.lastCid)
                        .setSyncChatChecksum(Prefs.syncChatChecksum)
                        .setFetchCount(SYNC_CHAT_FETCH_COUNT)
                        .build()
                    fetchedChatRooms = chatRemoteDataSource.syncChats(request)
                }
                fetchedChatRooms.delCidsList.also { if (it.isEmpty()) return@also }
                    .map { chatLocalDataSource.deleteChatRoomByCid(it) }
                chatLocalDataSource.saveChatRoom(
                    *fetchedChatRooms.updCidsList.map(::ChatRoom).toTypedArray()
                )
                return@runCatching chatLocalDataSource.getChatRooms()
            }
        }

    companion object {
        private const val SYNC_CHAT_FETCH_COUNT = 30
    }
}

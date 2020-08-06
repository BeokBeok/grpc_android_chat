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
                .setPeer(peerName)
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
            runCatching { refreshChatRooms(uid) }
        }

    private suspend fun refreshChatRooms(uid: String): List<ChatRoom> {
        val meta = Meta.newBuilder().setUid(uid).build()
        var fetchedChatRooms = fetchChatRooms(meta)

        while (!fetchedChatRooms.eof) {
            updateChatRooms(fetchedChatRooms)
            fetchedChatRooms = fetchChatRooms(meta)
        }
        updateChatRooms(fetchedChatRooms)
        return chatLocalDataSource.getChatRooms()
    }

    private suspend fun fetchChatRooms(meta: Meta?): SyncChatsResponse {
        val request = SyncChatsRequest.newBuilder()
            .setMeta(meta)
            .setLastCid(Prefs.lastCid)
            .setSyncChatChecksum(Prefs.syncChatChecksum)
            .setFetchCount(SYNC_CHAT_FETCH_COUNT)
            .build()
        return chatRemoteDataSource.syncChats(request)
    }

    private suspend fun updateChatRooms(syncChatsResponse: SyncChatsResponse) {
        chatLocalDataSource.updateChatRoom(syncChatsResponse)
        Prefs.lastCid = syncChatsResponse.lastCid
        Prefs.syncChatChecksum = syncChatsResponse.syncChatChecksum
    }

    companion object {
        private const val SYNC_CHAT_FETCH_COUNT = 30
    }
}

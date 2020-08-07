package com.example.grpc_android.data

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import com.example.grpc_android.data.entity.MessageEntity
import com.example.grpc_android.data.entity.mapToEntity
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

    override suspend fun getRooms(uid: String): Result<List<ChatRoom>> =
        withContext(ioDispatcher) {
            runCatching { chatLocalDataSource.getChatRooms() }
        }

    override suspend fun syncChats(uid: String): Result<List<ChatRoom>> =
        withContext(ioDispatcher) {
            runCatching { refreshChatRooms(uid) }
        }

    override suspend fun getMessages(cid: String): Result<ChatMessage> =
        runCatching { chatLocalDataSource.getChatMessage(cid) }

    override suspend fun syncLogs(uid: String, cid: String): Result<ChatMessage> =
        withContext(ioDispatcher) {
            runCatching {
                val cachedChatMessages =
                    chatLocalDataSource.getChatMessage(cid).messages.sortedBy { it.lid }
                val lastSyncLid = chatLocalDataSource.getLastSyncLid(cid)
                val lidRange = generateLidRange(cachedChatMessages, lastSyncLid)

                var syncLogsResponse = fetchChatMessages(
                    uid = uid,
                    cid = cid,
                    lastLid = cachedChatMessages.lastOrNull()?.lid ?: "0",
                    lidRange = lidRange
                )

                while (!syncLogsResponse.eof) {
                    chatLocalDataSource.updateChatMessage(cid, syncLogsResponse)
                    syncLogsResponse = fetchChatMessages(
                        uid = uid,
                        cid = cid,
                        lastLid = syncLogsResponse.messagesList.lastOrNull()?.lid ?: "0",
                        lidRange = generateLidRange(
                            syncLogsResponse.messagesList.map { it.mapToEntity() },
                            syncLogsResponse.lastSyncLid
                        )
                    )
                }
                chatLocalDataSource.updateChatMessage(cid, syncLogsResponse)
                return@runCatching chatLocalDataSource.getChatMessage(cid)
            }
        }

    private suspend fun fetchChatMessages(
        uid: String,
        cid: String,
        lastLid: String,
        lidRange: List<String>
    ): SyncLogsResponse {
        val meta = Meta.newBuilder().setUid(uid).setCid(cid).build()
        val request = SyncLogsRequest.newBuilder()
            .setMeta(meta)
            .setLastLid(lastLid)
            .setFetchCount(SYNC_LOGS_FETCH_COUNT)
            .addAllLidRanges(lidRange)
            .build()
        return chatRemoteDataSource.syncLogs(request)
    }

    private fun generateLidRange(
        cachedChatMessages: List<MessageEntity>,
        startLid: String
    ): List<String> =
        mutableListOf<String>().also {
            cachedChatMessages
                .filter { it.lid > startLid }
                .forEachIndexed { index, messageEntity ->
                    if (index + 1 > cachedChatMessages.lastIndex) return@forEachIndexed
                    if (messageEntity.lid == "0") return@forEachIndexed
                    if (messageEntity.lid != cachedChatMessages[index].prevLid) {
                        it.add(messageEntity.lid)
                        it.add(cachedChatMessages[index].lid)
                    }
                }
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
        private const val SYNC_LOGS_FETCH_COUNT = 1_000
    }
}

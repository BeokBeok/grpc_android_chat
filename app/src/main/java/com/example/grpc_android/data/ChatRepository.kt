package com.example.grpc_android.data

import com.example.grpc_android.data.entity.ChatMessage
import com.example.grpc_android.data.entity.ChatRoom
import io.grpc.chat.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun eventListen(uid: String): Flow<Receive>

    suspend fun chatWith(uid: String, peerName: String): Result<CreateResponse>

    suspend fun sendMessage(uid: String, cid: String, msg: String): Result<WriteResponse>

    suspend fun chatIn(uid: String, cid: String): Result<ChatInResponse>

    suspend fun chatOut(uid: String, cid: String): Result<ChatOutResponse>

    suspend fun getRooms(uid: String): Result<List<ChatRoom>>

    suspend fun syncChats(uid: String): Result<List<ChatRoom>>

    suspend fun getMessages(cid: String): Result<List<ChatMessage>>

    suspend fun syncLogs(uid: String, cid: String): Result<List<ChatMessage>>

    suspend fun saveMessage(cid: String, message: Message?)

    suspend fun join(uid: String, cid: String): Result<JoinResponse>
}

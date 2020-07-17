package com.example.grpc_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.android.AndroidChannelBuilder
import io.grpc.chat.*
import io.grpc.stub.MetadataUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val chatChannel: ManagedChannel by lazy {
        AndroidChannelBuilder.forAddress("qa-chat.conects.com", 10620)
            .context(applicationContext)
            .executor(Executors.newSingleThreadExecutor().asCoroutineDispatcher().executor)
            .build()
    }

    private val chatBlockingStub: ChatGrpcKt.ChatCoroutineStub by lazy {
        MetadataUtils.attachHeaders(
            ChatGrpcKt.ChatCoroutineStub(chatChannel),
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer yunsu"
                )
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEventListen()
        setupChatWith()
        setupChatIn()
        setupChatOut()
    }

    /**
     * 채팅 관련 각종 이벤트를 받을 수 있는 리스너 등록
     */
    private fun setupEventListen() {
        btn_event_listen.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val payloadModel = Payload.newBuilder().apply {
                    stream = OpenStream.newBuilder().apply {
                        name = "yunsu"
                    }.build()
                }.build()

                val request = Request.newBuilder().apply {
                    timestamp = Timestamp.getDefaultInstance()
                    payload = payloadModel
                }.build()

                chatBlockingStub.eventListen(request).collect { receive ->
                    println("$receive")
                }
            }
        }
    }

    /**
     * 채팅방 생성
     */
    private fun setupChatWith() {
        btn_chat_with.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val chatWithModel = ChatWith.newBuilder().apply {
                    name = "yunsu"
                    peer = name
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatWith = chatWithModel
                }.build()

                val request = Request.newBuilder().apply {
                    pldType = PayloadType.CHATWITH
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatWith(request) }
                println("cid is ${result.await().resp.cid}")
            }
        }
    }

    /**
     * 채팅방 진입
     */
    private fun setupChatIn() {
        btn_chat_in.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val chatInModel = ChatIn.newBuilder().apply {
                    uid = "yunsu"
                    chatPublicChecksum = "00000000000000000"
                    chatInChecksum = "00000000000000000"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatIn = chatInModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = "1000000005f102077101007"
                    pldType = PayloadType.CHATIN
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatIn(request) }
                println("cid is ${result.await().resp.cid}")
            }
        }
    }

    /**
     * 채팅방 퇴장
     */
    private fun setupChatOut() {
        btn_chat_out.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val chatOutModel = ChatOut.newBuilder().apply {
                    uid = "yunsu"
                    lastMsgLid = "0"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatOut = chatOutModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = "1000000005f102077101007"
                    pldType = PayloadType.CHATOUT
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatOut(request) }
                println("cid is ${result.await().resp.cid}")
            }
        }
    }
}
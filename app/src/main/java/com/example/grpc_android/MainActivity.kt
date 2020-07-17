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

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEventListen()
        setupChatWith()
        setupChatIn()
        setupChatOut()
        setupSendMessage()
    }

    /**
     * 채팅 관련 각종 이벤트를 받을 수 있는 리스너 등록
     */
    private fun setupEventListen() {
        btn_event_listen.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val payloadModel = Payload.newBuilder().apply {
                    stream = OpenStream.newBuilder().apply {
                        name = tiet_uid.text.toString()
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
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val chatWithModel = ChatWith.newBuilder().apply {
                    name = tiet_uid.text.toString()
                    peer = "test1"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatWith = chatWithModel
                }.build()

                val request = Request.newBuilder().apply {
                    pldType = PayloadType.CHATWITH
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatWith(request) }
                withContext(Dispatchers.Main) {
                    tiet_cid.setText(result.await().resp.cid)
                }
            }
        }
    }

    /**
     * 채팅방 진입
     */
    private fun setupChatIn() {
        btn_chat_in.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val chatInModel = ChatIn.newBuilder().apply {
                    uid = tiet_uid.text.toString()
                    chatPublicChecksum = "00000000000000000"
                    chatInChecksum = "00000000000000000"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatIn = chatInModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
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
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val chatOutModel = ChatOut.newBuilder().apply {
                    uid = tiet_uid.text.toString()
                    lastMsgLid = "0"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatOut = chatOutModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
                    pldType = PayloadType.CHATOUT
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatOut(request) }
                println("cid is ${result.await().resp.cid}")
            }
        }
    }

    /**
     * 메시지 전송
     */
    private fun setupSendMessage() {
        btn_send_message.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val sendMessageModel = Message.newBuilder().apply {
                    uid = tiet_uid.text.toString()
                    message = tiet_message.text.toString()
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    message = sendMessageModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
                    pldType = PayloadType.MESSAGE
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.sendMessage(request) }
                println("cid is ${result.await().resp}")
            }
        }
    }
}
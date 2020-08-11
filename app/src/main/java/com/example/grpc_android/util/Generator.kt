package com.example.grpc_android.util

object Generator {

    fun generateTid(): String {
        val prefix = "1"
        val timeStamp = "${System.currentTimeMillis()}000"
        val lidType = "1"
        val serverId = "00"
        val cyclingIncrementalNumberBit = "000"
        return prefix + timeStamp + lidType + serverId + cyclingIncrementalNumberBit
    }
}

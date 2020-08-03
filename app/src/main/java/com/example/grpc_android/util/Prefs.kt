package com.example.grpc_android.util

import androidx.preference.PreferenceManager
import com.example.grpc_android.MyApplication

object Prefs {

    private const val LAST_CID = "last_cid"
    private const val SYNC_CHAT_CHECKSUM = "sync_chat_checksum"

    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.instance)
    }

    var lastCid
        get() = prefs.getString(LAST_CID, "")
        set(value) = prefs.edit().putString(LAST_CID, value ?: "").apply()

    var syncChatChecksum
        get() = prefs.getString(SYNC_CHAT_CHECKSUM, "")
        set(value) = prefs.edit().putString(SYNC_CHAT_CHECKSUM, value ?: "").apply()
}

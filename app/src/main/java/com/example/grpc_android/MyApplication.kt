package com.example.grpc_android

import android.app.Application
import org.conscrypt.Conscrypt
import java.security.Security

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Security.insertProviderAt(Conscrypt.newProvider(), 1)
    }
}
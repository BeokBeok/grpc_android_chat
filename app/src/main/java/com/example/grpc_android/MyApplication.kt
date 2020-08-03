package com.example.grpc_android

import com.example.grpc_android.di.DaggerAppComponent
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.conscrypt.Conscrypt
import java.security.Security

class MyApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Security.insertProviderAt(Conscrypt.newProvider(), 1)
        setupFlipper()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    private fun setupFlipper() {
        if (BuildConfig.DEBUG) {
            SoLoader.init(this, false)
            AndroidFlipperClient.getInstance(this).also {
                it.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
                it.addPlugin(DatabasesFlipperPlugin(this))
                it.addPlugin(SharedPreferencesFlipperPlugin(this))
            }.start()
        }
    }

    companion object {
        lateinit var instance: MyApplication
    }
}
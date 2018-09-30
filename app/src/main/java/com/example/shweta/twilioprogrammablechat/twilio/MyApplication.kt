package com.example.shweta.twilioprogrammablechat.twilio

import android.app.Application

/**
 * Created by Shweta on 30-09-2018.
 */
class MyApplication : Application() {
    companion object {
        var mGetRequestLastSeen = ""
        var isHomeActivityActive = false

        lateinit var instance: MyApplication
            private set
    }

    lateinit var clientManager: ChatClientManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        /*com.blankj.utilcode.util.Utils.init(this) // Init Utils
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.IS_FABRIC_DISABLED).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)*/
        clientManager = ChatClientManager(applicationContext)
    }
}
package com.app.smartaccounting.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.google.firebase.FirebaseApp


class Accounting : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        Accounting.appContext = applicationContext
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    companion object {

        lateinit  var appContext: Context

    }



}
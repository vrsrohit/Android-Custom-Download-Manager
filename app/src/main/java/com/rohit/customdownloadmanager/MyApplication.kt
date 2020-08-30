package com.rohit.customdownloadmanager

import android.app.Application
import android.content.Context

open class MyApplication : Application() {

    private var appContext: Context? = null

    override fun onCreate() {
        super.onCreate()
    }

}
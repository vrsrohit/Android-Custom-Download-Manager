package com.rohit.customdownloadmanager.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object DatabaseUtils {

    fun defaultGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
}
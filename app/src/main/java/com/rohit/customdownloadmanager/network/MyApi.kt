package com.rohit.customdownloadmanager.network

import com.rohit.customdownloadmanager.MyApplication

object MyApi {
    val retrofitService: WebService by lazy {
        MyApplication().getRetorfitInstance().create(WebService::class.java)
    }
}
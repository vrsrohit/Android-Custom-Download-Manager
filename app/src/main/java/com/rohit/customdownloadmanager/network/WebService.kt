package com.rohit.customdownloadmanager.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface WebService {

    @GET
    @Streaming
    fun getDownloadData(@Url url: String): Call<ResponseBody>
}


package com.rohit.customdownloadmanager.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.network.MyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*


class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val dataDatabase = MyDatabase.getInstance(applicationContext)

    private val myApi = MyApi.retrofitService


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.i(TAG, "doWork: dsds")
            performWork()
            Result.success()
        } catch (error: Throwable) {
            error.printStackTrace()
            Result.failure()
        }
    }


    private suspend fun performWork() {
        val downloadList = getActiveDownloadList()
        if (downloadList.isNotEmpty()) {
            performDownloadTasks(downloadList = downloadList)
        } else {
            return
        }
    }

    private suspend fun performDownloadTasks(downloadList: List<DownloadDetail>) {
        for (download in downloadList) {
            if (download.downloadStatus != DownloadStatus.Completed.name) {
                downloadFile2(download)
            }
        }
        performWork()
    }

    private fun downloadFile(downloadDetail: DownloadDetail) {

        val responseBody = myApi.getDownloadData(downloadDetail.url).execute()
        try {
            //you can now get your file in the InputStream
            val inputStream = responseBody.body()?.byteStream()
            inputStream?.let {
                val file = File(downloadDetail.filePath)
                file.copyInputStreamToFile(inputStream)
                saveFile(downloadDetail, file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        saveFile(downloadDetail, File(""))
    }

    private fun downloadFile2(downloadDetail: DownloadDetail) {
        val client = OkHttpClient()
        val request = Request.Builder().url(downloadDetail.url)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()
        if (response.body != null) {
            /* val inpustStream: InputStream = response.body!!.byteStream()
             val reader = BufferedReader(InputStreamReader(inpustStream))
             var result: String?
             var line: String? = reader.readLine()
             result = line
             while (reader.readLine().also { line = it } != null) {
                 result += line
             }
             println(result)
             response.body!!.close()
         }*/
            try {
                //you can now get your file in the InputStream
                val inputStream = response.body?.byteStream()
                inputStream?.let {
                    val file = File(downloadDetail.filePath)
                    file.copyInputStreamToFile(inputStream)
                    saveFile(downloadDetail, file)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        saveFile(downloadDetail, File(""))
    }


    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    private fun saveFile(downloadDetail: DownloadDetail, file: File) {
        if (file.exists()) {
            file.delete()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getActiveDownloadList(): List<DownloadDetail> {
        return dataDatabase.downloadDao.getPendingDownloads()
    }


}
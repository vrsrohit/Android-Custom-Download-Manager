package com.rohit.customdownloadmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.utils.HelperExtensions.logi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream


class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val dataDatabase = MyDatabase.getInstance(applicationContext)


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            logi("work started")
            performWork()
            logi("work ended")
            Result.success()
        } catch (error: Throwable) {
            logi("work failed")
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
                downloadFile(download)
            }
        }
        performWork()
    }

    private suspend fun downloadFile(downloadDetail: DownloadDetail) {

        val client = OkHttpClient()
        val request = Request.Builder().url(downloadDetail.url)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()

        if (response.body != null) {
            try {
                val inputStream = response.body?.byteStream()
                inputStream?.let {
                    val file = File(downloadDetail.filePath)
                    file.copyInputStreamToFile(inputStream)
                    inputStream.close()
                    downloadDetail.downloadStatus = DownloadStatus.Completed.name
                    dataDatabase.downloadDao.updateDownloadDetail(downloadDetail)
                    logi(downloadDetail.fileName + " - download done")
                }

            } catch (e: IOException) {
                e.printStackTrace()
                updateDownloadFailed(downloadDetail)
            }
        } else {
            logi(downloadDetail.fileName + " - response body null - download failed")
            updateDownloadFailed(downloadDetail)
        }
    }

    private suspend fun updateDownloadFailed(downloadDetail: DownloadDetail) {
        downloadDetail.downloadStatus = DownloadStatus.Stopped.name
        dataDatabase.downloadDao.updateDownloadDetail(downloadDetail)
        logi(downloadDetail.fileName + " - download failed")
    }


    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    private suspend fun getActiveDownloadList(): List<DownloadDetail> {
        return dataDatabase.downloadDao.getPendingDownloads()
    }


}
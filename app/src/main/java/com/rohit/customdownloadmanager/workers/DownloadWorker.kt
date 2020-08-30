package com.rohit.customdownloadmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.utils.FileUtils
import com.rohit.customdownloadmanager.utils.HelperExtensions.logi
import com.rohit.customdownloadmanager.utils.WorkerUtils.sendStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream


@Suppress("BlockingMethodInNonBlockingContext")
class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val dataDatabase = MyDatabase.getInstance(applicationContext)


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            showFeedback(message = "work started")
            performWork()
            showFeedback(message = "work ended")
            Result.success()
        } catch (error: Throwable) {
            showFeedback(message = "work failed")
            error.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun performWork() {
        val downloadDetailList = getActiveDownloadList()
        logi(message = "download list size -> " + downloadDetailList.size.toString())
        if (downloadDetailList.isNotEmpty()) {
            performDownloadTasks(downloadDetailList = downloadDetailList)
        } else {
            return
        }
    }

    private suspend fun performDownloadTasks(downloadDetailList: List<DownloadDetail>) {
        for (downloadDetail in downloadDetailList) {
            if (downloadDetail.downloadStatus != DownloadStatus.Completed) {
                showFeedback(message = downloadDetail.fileName + " - download started")
                downloadFile(downloadDetail = downloadDetail)
            }
        }
        performWork()
    }

    private suspend fun downloadFile(downloadDetail: DownloadDetail) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(downloadDetail.url)
                .addHeader("Content-Type", "application/json")
                .build()
            val response = client.newCall(request).execute()

            if (response.body != null) {
                val file = FileUtils.createFile(
                    applicationContext,
                    fileType = downloadDetail.fileType,
                    downloadDetail.fileName
                )
                if (file != null) {
                    val inputStream = response.body?.byteStream()
                    inputStream?.let {
                        file.copyInputStreamToFile(inputStream)
                        inputStream.close()
                        downloadDetail.downloadStatus = DownloadStatus.Completed
                        dataDatabase.downloadDao.updateDownloadDetail(downloadDetail = downloadDetail)
                        showFeedback(message = downloadDetail.fileName + " - download success")
                        return
                    }
                } else {
                    logi(message = downloadDetail.fileName + " - response body null - download failed")
                    updateDownloadFailed(downloadDetail = downloadDetail)
                }
            } else {
                logi(message = downloadDetail.fileName + " - response body null - download failed")
                updateDownloadFailed(downloadDetail = downloadDetail)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            updateDownloadFailed(downloadDetail = downloadDetail)
        } catch (e: Exception) {
            e.printStackTrace()
            updateDownloadFailed(downloadDetail = downloadDetail)
        }

    }

    private fun updateDownloadFailed(downloadDetail: DownloadDetail) {
        /*downloadDetail.downloadStatus = DownloadStatus.Stopped
        dataDatabase.downloadDao.updateDownloadDetail(downloadDetail)*/
        val message = downloadDetail.fileName + " - download failed"
        showFeedback(message)
    }

    private fun showFeedback(message: String) {
        logi(message = message)
        sendStatusNotification(message = message, context = applicationContext)
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
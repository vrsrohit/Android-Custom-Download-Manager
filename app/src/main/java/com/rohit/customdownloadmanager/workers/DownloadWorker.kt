package com.rohit.customdownloadmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.utils.FileUtils
import com.rohit.customdownloadmanager.utils.HelperExtensions.logi
import com.rohit.customdownloadmanager.utils.NotificationUtils.sendStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.sql.Timestamp
import java.util.*


@Suppress("BlockingMethodInNonBlockingContext")
class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val myDatabase = MyDatabase.getInstance(applicationContext)


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            logi(message = "work started")
            performWork()
            logi(message = "work ended")
            Result.success()
        } catch (error: Throwable) {
            logi(message = "work failed")
            error.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun performWork() {
        val downloadDetail = getActiveDownload()
        if (downloadDetail != null && downloadDetail.downloadStatus != DownloadStatus.Completed) {
            showFeedback(message = downloadDetail.fileName + " - download started")
            downloadFile(downloadDetail)
            performWork()
        } else {
            return
        }
    }

    private suspend fun downloadFile(downloadDetail: DownloadDetail) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(downloadDetail.url)
                .addHeader("Content-Type", "application/json")
                .build()
            val response = client.newCall(request).execute()

            if (response.body != null) {
                if (downloadDetail.filePath.isNotEmpty()) {
                    val file = File(downloadDetail.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                val file = FileUtils.createFile(
                    applicationContext,
                    fileType = downloadDetail.fileType,
                    downloadDetail.fileName
                )
                if (file != null) {
                    downloadDetail.filePath = file.absolutePath
                    myDatabase.downloadDao.updateDownloadDetail(downloadDetail = downloadDetail)
                    val inputStream = response.body?.byteStream()
                    inputStream?.let {
                        file.copyInputStreamToFile(inputStream)
                        inputStream.close()
                        downloadDetail.downloadStatus = DownloadStatus.Completed
                        myDatabase.downloadDao.updateDownloadDetail(downloadDetail = downloadDetail)
                        val message =
                            downloadDetail.fileName + " - download success" + downloadDetail.filePath
                        showFeedback(message = message)
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
        val message = downloadDetail.fileName + " - download failed"
        showFeedback(message)
    }

    private fun showFeedback(message: String) {
        logi(message = message)
        sendStatusNotification(message = message, context = applicationContext,  Timestamp(Date().time).nanos)
    }


    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }

    private suspend fun getActiveDownload(): DownloadDetail? {
        return myDatabase.downloadDao.getPendingDownload()
    }


}
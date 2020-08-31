package com.rohit.customdownloadmanager

import android.content.Context
import androidx.work.*
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.enums.FileType
import com.rohit.customdownloadmanager.workers.DownloadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class CustomDownloadManager(
    private var downloadId: Int,
    private var fileName: String,
    private var fileType: FileType,
    private var url: String,
    private var priority: Int,
    context: Context
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var database = MyDatabase.getInstance(context)
    private val workManager = WorkManager.getInstance(context)

    fun enqueueDownload() {
        val downloadDetail = DownloadDetail(
            downloadId = downloadId,
            fileName = fileName,
            fileType = fileType,
            url = url,
            createdTime = Timestamp(Date().time).nanos,
            priority = priority,
            downloadStatus = DownloadStatus.Enqueued
        )
        addDownloadRequestToDatabase(downloadDetail = downloadDetail)
        startWorker()
    }

    private fun addDownloadRequestToDatabase(downloadDetail: DownloadDetail) {
        coroutineScope.launch {
            database.downloadDao.insertDownloadRecord(downloadDetail)
        }
    }

    private fun startWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val downloadRequest = OneTimeWorkRequest
            .Builder(DownloadWorker::class.java)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            "downloadWorker",
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )

    }

}

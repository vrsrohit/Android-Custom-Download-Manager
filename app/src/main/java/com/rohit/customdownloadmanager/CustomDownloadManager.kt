package com.rohit.customdownloadmanager

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.enums.FileType
import com.rohit.customdownloadmanager.workers.DownloadWorker
import kotlinx.coroutines.runBlocking
import java.sql.Timestamp
import java.util.*

class CustomDownloadManager(
    private var fileName: String,
    private var fileType: FileType,
    private var url: String,
    private var priority: Int
) : MyApplication() {

    private val database = MyDatabase.getInstance(applicationContext)

    /* //Test links
    private val pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
    private val videoUrl =
        "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4"*/

    fun enqueueDownload() {
        val downloadDetail = DownloadDetail(
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
        runBlocking {
            database.downloadDao.insertDownloadRecord(downloadDetail)
        }
    }

    private fun startWorker() {
        runBlocking {
            if (!isWorkerALive()) {
                val downloadRequest = OneTimeWorkRequest
                    .Builder(DownloadWorker::class.java)
                    .build()
                val workManager = WorkManager.getInstance(applicationContext)
                workManager.enqueueUniqueWork(
                    "downloadWorker",
                    ExistingWorkPolicy.REPLACE,
                    downloadRequest
                )
            }
        }

    }

    private suspend fun isWorkerALive(): Boolean {
        val pendingDownloads = database.downloadDao.getPendingDownloads()
        return pendingDownloads.isNotEmpty()
    }


}

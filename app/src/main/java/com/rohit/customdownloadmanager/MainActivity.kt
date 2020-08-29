package com.rohit.customdownloadmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.enums.FileType
import com.rohit.customdownloadmanager.workers.DownloadWorker
import kotlinx.coroutines.runBlocking
import com.rohit.customdownloadmanager.utils.FileUtils

class MainActivity : AppCompatActivity() {
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var database: MyDatabase

    private val pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
    private val videoUrl =
        "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                200
            )
        } else {
            scheduleTasks()
        }

    }

    private fun scheduleTasks() {
        val file1 = FileUtils.createFile(this, FileType.Pdf, "pdf1")
        val file2 = FileUtils.createFile(this, FileType.Pdf, "pdf2")
        val file3 = FileUtils.createFile(this, FileType.Video_mp4, "video1")
        val file4 = FileUtils.createFile(this, FileType.Video_mp4, "video2")
        val downloadDetail1 = DownloadDetail(
            downloadId = 1001,
            fileName = "pdf1",
            url = pdfUrl,
            filePath = file1!!.absolutePath,
            createdTime = 1212121212,
            priority = 1,
            downloadStatus = DownloadStatus.Waiting.name,
            downloadProgress = "10"
        )
        val downloadDetail2 = DownloadDetail(
            downloadId = 1002,
            fileName = "pdf2",
            url = pdfUrl,
            filePath = file2!!.absolutePath,
            createdTime = 1212121212,
            priority = 2,
            downloadStatus = DownloadStatus.Waiting.name,
            downloadProgress = "10"
        )
        val downloadDetail3 = DownloadDetail(
            downloadId = 1003,
            fileName = "video1",
            url = videoUrl,
            filePath = file3!!.absolutePath,
            createdTime = 1212121212,
            priority = 3,
            downloadStatus = DownloadStatus.Waiting.name,
            downloadProgress = "10"
        )
        val downloadDetail4 = DownloadDetail(
            downloadId = 10004,
            fileName = "video2",
            url = videoUrl,
            filePath = file4!!.absolutePath,
            createdTime = 1212121212,
            priority = 4,
            downloadStatus = DownloadStatus.Waiting.name,
            downloadProgress = "10"
        )
        database = MyDatabase.getInstance(applicationContext)
        runBlocking {
            database.downloadDao.deleteAll()
            database.downloadDao.insertDownloadRecord(downloadDetail1)
            database.downloadDao.insertDownloadRecord(downloadDetail2)
            database.downloadDao.insertDownloadRecord(downloadDetail3)
            database.downloadDao.insertDownloadRecord(downloadDetail4)
        }
        val downloadRequest = OneTimeWorkRequest
            .Builder(DownloadWorker::class.java)
            .build()
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            "downloadWorker",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            downloadRequest
        )
    }

    private fun hasPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

}
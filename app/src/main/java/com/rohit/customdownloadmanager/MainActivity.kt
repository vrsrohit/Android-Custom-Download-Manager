package com.rohit.customdownloadmanager

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.RoomDatabase
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rohit.customdownloadmanager.database.MyDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.workers.DownloadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var database: MyDatabase

    private val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                200
            )
        }
        val file = com.rohit.customdownloadmanager.utils.FileUtils.createFile(this)

        val downloadDetail = DownloadDetail(
            downloadId = 1000,
            url = url,
            filePath = file!!.absolutePath,
            createdTime = 1212121212,
            priority = 1,
            downloadStatus = DownloadStatus.Waiting.name,
            downloadProgress = "10"
        )
        database = MyDatabase.getInstance(applicationContext)
        runBlocking {
            database.downloadDao.insertDownloadRecord(downloadDetail)
        }

        val downloadRequest = OneTimeWorkRequest
            .Builder(DownloadWorker::class.java)
            .build()
        val workManager = WorkManager.getInstance(application)
        workManager.enqueue(downloadRequest)

    }

    private fun hasPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

}
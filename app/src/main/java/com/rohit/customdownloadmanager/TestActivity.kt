package com.rohit.customdownloadmanager

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.util.*

class TestActivity : AppCompatActivity() {

    private lateinit var onDownloadComplete: BroadcastReceiver
    private var downloadId: Long = 0

    private val mPERMISSIONSREQUIRED = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!hasCameraPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                mPERMISSIONSREQUIRED,
                200
            )
        }
        val btnDownload = findViewById<Button>(R.id.download_btn)
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

        val file = createFile()
        val downloadRequest =
            createDownloadManagerRequest(url = url, file = file!!)


        onDownloadComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                id?.let {
                    if (id == downloadId) {
                        Toast.makeText(this@TestActivity, "Download Completed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        btnDownload.setOnClickListener {
            downloadId = downloadManager.enqueue(downloadRequest)
            Log.i("Download Id", downloadId.toString())
        }


    }

    private fun createDownloadManagerRequest(url: String, file: File): DownloadManager.Request {
        Log.i("path", Uri.fromFile(file).toString())
        return DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
            .setTitle("Testing")
            .setDescription("Description")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

    }

    private fun createFile(): File? {
        val dir = File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "TestDownloads")
        try {
            var success = true
            if (!dir.exists()) {
                success = dir.mkdirs()
            }
            return if (success) {
                File(
                    dir.absolutePath
                            + File.separator
                            + Timestamp(Date().time).toString()
                            + "test_download.pdf"
                )
            } else {
                null
            }
        } catch (e: IOException) {
            return null
        } catch (e: SecurityException) {
            return null
        }
    }

    private fun hasCameraPermissions() = mPERMISSIONSREQUIRED.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }


}


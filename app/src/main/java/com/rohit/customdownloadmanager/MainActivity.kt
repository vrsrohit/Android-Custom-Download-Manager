package com.rohit.customdownloadmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.rohit.customdownloadmanager.databinding.ActivityMainBinding
import com.rohit.customdownloadmanager.enums.FileType
import com.rohit.customdownloadmanager.utils.FileUrls
import com.rohit.customdownloadmanager.utils.HelperExtensions.toastShort
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var mBinding: ActivityMainBinding

    private val mFileTypes = arrayOf(
        FileType.Pdf.name,
        FileType.Mp4.name,
        FileType.Mp3.name,
        FileType.Docx.name,
        FileType.Png.name,
        FileType.Jpg.name
    )

    private var mSelectedFileType: FileType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (!hasPermissions()) {
            requestPermissions()
        }

        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, mFileTypes)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerFileType.adapter = arrayAdapter

        mBinding.tvDownloadPdf.setOnClickListener {
            enqueueSampleDownload(FileType.Pdf)
        }

        mBinding.tvDownloadVideo.setOnClickListener {
            enqueueSampleDownload(FileType.Mp4)
        }

        mBinding.tvDownloadAudio.setOnClickListener {
            enqueueSampleDownload(FileType.Mp3)
        }

        mBinding.tvDownloadDocx.setOnClickListener {
            enqueueSampleDownload(FileType.Docx)
        }

        mBinding.tvDownloadPng.setOnClickListener {
            enqueueSampleDownload(FileType.Png)
        }

        mBinding.tvDownloadJpg.setOnClickListener {
            enqueueSampleDownload(FileType.Jpg)
        }

        mBinding.spinnerFileType.onItemSelectedListener = this

        mBinding.tvDownload.setOnClickListener {
            enqueueCustomDownload()
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mSelectedFileType = getFileType(fileType = mFileTypes[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        mSelectedFileType = null
    }

    private fun enqueueCustomDownload() {
        if (hasPermissions()) {
            if (mBinding.etUrl.text.isEmpty()) {
                toastShort(message = "Please enter a valid Url")
                return
            }

            if (mBinding.etFileName.text.isEmpty()) {
                toastShort(message = "Please enter a valid file name")
                return
            }

            if (mBinding.etPriority.text.isEmpty()) {
                toastShort(message = "Please enter a valid priority")
                return
            }

            if (mSelectedFileType == null) {
                toastShort(message = "Please select a valid file type")
                return
            }

            val url = mBinding.etUrl.text.toString()
            val fileName = mBinding.etFileName.text.toString()
            val priority = mBinding.etPriority.text.toString().toInt()

            mSelectedFileType?.let { fileType ->
                val customDownloadManager = CustomDownloadManager(
                    downloadId = Timestamp(Date().time).nanos,
                    url = url,
                    fileName = fileName,
                    priority = priority,
                    fileType = fileType,
                    context = applicationContext
                )
                customDownloadManager.enqueueDownload()
                toastShort(message = "Download Enqueued")
            }
        } else {
            requestPermissions()
        }

    }

    private fun enqueueSampleDownload(fileType: FileType) {
        if (hasPermissions()) {
            var url = ""
            var fileName = ""
            var priority = 1

            when (fileType) {
                FileType.Pdf -> {
                    url = FileUrls.pdf_1mb
                    fileName = "Test_pdf"
                    priority = 1
                }
                FileType.Mp4 -> {
                    url = FileUrls.video_mp4_10mb
                    fileName = "Test_video"
                    priority = 2
                }
                FileType.Mp3 -> {
                    url = FileUrls.audio_mp3_5mb
                    fileName = "Test_audio"
                    priority = 3
                }
                FileType.Docx -> {
                    url = FileUrls.docx_1mb
                    fileName = "Test_doc"
                    priority = 4
                }
                FileType.Png -> {
                    url = FileUrls.png_3mb
                    fileName = "Test_png"
                    priority = 5
                }
                FileType.Jpg -> {
                    url = FileUrls.jpg_2mb
                    fileName = "Test_jpg"
                    priority = 5
                }

            }
            val customDownloadManager = CustomDownloadManager(
                downloadId = Timestamp(Date().time).nanos,
                url = url,
                fileName = fileName,
                priority = priority,
                fileType = fileType,
                context = applicationContext
            )
            customDownloadManager.enqueueDownload()
            toastShort(message = "Download Enqueued")
        } else {
            requestPermissions()
        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            200
        )
    }

    private fun hasPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getFileType(fileType: String): FileType {
        return when (fileType) {
            FileType.Pdf.name -> {
                FileType.Pdf
            }
            FileType.Mp4.name -> {
                FileType.Mp4
            }
            FileType.Mp3.name -> {
                FileType.Mp3
            }
            FileType.Docx.name -> {
                FileType.Docx
            }
            FileType.Png.name -> {
                FileType.Png
            }
            FileType.Jpg.name -> {
                FileType.Jpg
            }
            else -> {
                FileType.Pdf
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    toastShort("Permissions granted")
                } else {
                    toastShort("Permission requests denied")
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 10
    }

}
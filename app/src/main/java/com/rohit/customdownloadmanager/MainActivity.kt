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
import com.rohit.customdownloadmanager.utils.HelperExtensions.toastShort
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var mBinding: ActivityMainBinding

    private val mFileTypes = arrayOf(FileType.Pdf.name, FileType.Video_mp4.name)

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
            enqueueSamplePdfDownload()
        }

        mBinding.tvDownloadVideo.setOnClickListener {
            enqueueSampleVideoDownload()
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

            if (!mBinding.spinnerFileType.isActivated || mSelectedFileType == null) {
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

    private fun enqueueSamplePdfDownload() {
        if (hasPermissions()) {
            val pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
            val customDownloadManager = CustomDownloadManager(
                downloadId = Timestamp(Date().time).nanos,
                url = pdfUrl,
                fileName = "Test_Pdf",
                priority = 1,
                fileType = FileType.Pdf,
                context = applicationContext
            )
            customDownloadManager.enqueueDownload()
            toastShort(message = "Download Enqueued")
        } else {
            requestPermissions()
        }
    }

    private fun enqueueSampleVideoDownload() {
        if (hasPermissions()) {
            val videoUrl =
                "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4"
            val customDownloadManager = CustomDownloadManager(
                downloadId = Timestamp(Date().time).nanos,
                url = videoUrl,
                fileName = "Test_Video",
                priority = 1,
                fileType = FileType.Video_mp4,
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
            FileType.Video_mp4.name -> {
                FileType.Video_mp4
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
package com.rohit.customdownloadmanager.utils

import android.content.Context
import android.os.Environment
import com.rohit.customdownloadmanager.enums.FileType
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.util.*

object FileUtils {

    fun createFile(context: Context, fileType: FileType, fileName: String): File? {
        val dir = File(
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "CustomDownloads"
        )
        try {
            var success = true
            if (!dir.exists()) {
                success = dir.mkdirs()
            }
            return if (success) {
                val file = File(
                    dir.absolutePath
                            + File.separator
                            + createFileName(fileType, fileName)
                )
                if (file.exists()) {
                    file.delete()
                }
                file
            } else {
                null
            }
        } catch (e: IOException) {
            return null
        } catch (e: SecurityException) {
            return null
        }
    }

    private fun createFileName(fileType: FileType, fileName: String): String {
        return when (fileType) {
            FileType.Pdf -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_download.pdf"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".pdf"
                }
            }
            FileType.Video_mp4 -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_video.mp4"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".mp4"
                }
            }
        }
    }
}
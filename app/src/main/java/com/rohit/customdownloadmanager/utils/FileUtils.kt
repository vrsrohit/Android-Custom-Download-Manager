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
                    Timestamp(Date().time).toString() + "test_pdf.pdf"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".pdf"
                }
            }
            FileType.Mp4 -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_video.mp4"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".mp4"
                }
            }
            FileType.Mp3 -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_audio.mp3"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".mp3"
                }
            }
            FileType.Docx -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_doc.docx"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".docx"
                }
            }
            FileType.Png -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_png.png"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".png"
                }
            }
            FileType.Jpg -> {
                if (fileName.isEmpty()) {
                    Timestamp(Date().time).toString() + "test_jpg.jpg"
                } else {
                    Timestamp(Date().time).toString() + fileName + ".jpg"
                }
            }
        }
    }
}
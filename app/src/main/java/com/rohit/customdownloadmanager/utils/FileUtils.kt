package com.rohit.customdownloadmanager.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.util.*

object FileUtils {
    fun createFile(context: Context): File? {
        val dir = File(
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "TestDownloads"
        )
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
}
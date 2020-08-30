package com.rohit.customdownloadmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohit.customdownloadmanager.enums.DownloadStatus
import com.rohit.customdownloadmanager.enums.FileType

@Entity
data class DownloadDetail(
    @PrimaryKey(autoGenerate = true)
    val downloadId: Int,
    var fileName: String,
    var fileType:FileType,
    var url: String,
    var createdTime: Int,
    var priority: Int,
    var downloadStatus: DownloadStatus
) {
    var filePath: String = ""
}
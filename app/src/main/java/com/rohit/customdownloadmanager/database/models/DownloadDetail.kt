package com.rohit.customdownloadmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohit.customdownloadmanager.enums.DownloadStatus
import java.io.File

@Entity
data class DownloadDetail(
    @PrimaryKey(autoGenerate = true)
    var downloadId: Int,
    var url: String,
    var filePath: String,
    var createdTime: Long,
    var downloadStatus: DownloadStatus,
    var downloadProgress: Int,
    var completedSize: Long,
    var file: File
)
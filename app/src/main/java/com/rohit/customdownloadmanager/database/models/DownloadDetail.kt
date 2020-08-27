package com.rohit.customdownloadmanager.database.models

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
    var priority:Int,
    var downloadStatus: DownloadStatus,
    var downloadProgress: String
)
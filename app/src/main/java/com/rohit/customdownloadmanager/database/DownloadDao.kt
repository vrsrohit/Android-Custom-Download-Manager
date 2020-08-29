package com.rohit.customdownloadmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus

@Dao
abstract class DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDownloadRecord(downloadDetail: DownloadDetail)

    @Query("SELECT * FROM DownloadDetail ORDER BY priority ASC")
    abstract fun getAllDownloads(): List<DownloadDetail>

    @Query("SELECT * FROM DownloadDetail WHERE downloadStatus LIKE :downloadStatus ORDER BY priority ASC ")
    abstract fun getPendingDownloads(downloadStatus: String = DownloadStatus.Waiting.name): List<DownloadDetail>

    @Query("SELECT * FROM DownloadDetail WHERE downloadStatus LIKE :downloadStatus ORDER BY priority ASC ")
    abstract fun getCompletedDownloads(downloadStatus: String = DownloadStatus.Completed.name): LiveData<List<DownloadDetail>>


}
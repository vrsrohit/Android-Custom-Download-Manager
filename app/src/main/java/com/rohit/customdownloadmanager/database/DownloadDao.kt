package com.rohit.customdownloadmanager.database

import androidx.room.*
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.enums.DownloadStatus

@Dao
abstract class DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDownloadRecord(downloadDetail: DownloadDetail)

    @Query("DELETE FROM DownloadDetail")
    abstract suspend fun deleteAll()
    @Update
    abstract suspend fun updateDownloadDetail(downloadDetail: DownloadDetail)

    @Query("SELECT * FROM DownloadDetail ORDER BY priority ASC")
    abstract suspend fun getAllDownloads(): List<DownloadDetail>

    @Query("SELECT * FROM DownloadDetail WHERE downloadStatus LIKE :downloadStatus ORDER BY priority DESC ")
    abstract suspend fun getPendingDownloads(downloadStatus: String = DownloadStatus.Waiting.name): List<DownloadDetail>

    @Query("SELECT * FROM DownloadDetail WHERE downloadStatus LIKE :downloadStatus ORDER BY priority ASC ")
    abstract suspend fun getCompletedDownloads(downloadStatus: String = DownloadStatus.Completed.name): List<DownloadDetail>


}
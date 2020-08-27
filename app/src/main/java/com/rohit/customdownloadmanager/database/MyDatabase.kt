package com.rohit.customdownloadmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rohit.customdownloadmanager.database.models.DownloadDetail


@Database(
    entities = [DownloadDetail::class],
    version = 1
)
abstract class MyDatabase : RoomDatabase() {
}
package com.rohit.customdownloadmanager.database.converters


import androidx.room.TypeConverter
import com.rohit.customdownloadmanager.database.models.DownloadDetail
import com.rohit.customdownloadmanager.utils.DatabaseUtils.defaultGson


class DownloadDetailConverter {

    @TypeConverter
    fun stringToDownloadDetail(data: String?): DownloadDetail? {
        if (data == null) {
            return null
        }
        val gson = defaultGson()
        return gson.fromJson(data, DownloadDetail::class.java)
    }

    @TypeConverter
    fun downloadDetailToString(downloadDetail: DownloadDetail?): String? {
        if (downloadDetail == null) {
            return null
        }
        val gson = defaultGson()
        return gson.toJson(downloadDetail)
    }
}

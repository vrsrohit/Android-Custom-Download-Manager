package com.rohit.customdownloadmanager.database.converters


import androidx.room.TypeConverter
import com.rohit.customdownloadmanager.enums.FileType
import com.rohit.customdownloadmanager.utils.DatabaseUtils.defaultGson


class FileTypeConverter {

    @TypeConverter
    fun stringToFileType(data: String?): FileType? {
        if (data == null) {
            return null
        }
        val gson = defaultGson()
        return gson.fromJson(data, FileType::class.java)
    }

    @TypeConverter
    fun fileTypeToString(fileType: FileType?): String? {
        if (fileType == null) {
            return null
        }
        val gson = defaultGson()
        return gson.toJson(fileType)
    }
}

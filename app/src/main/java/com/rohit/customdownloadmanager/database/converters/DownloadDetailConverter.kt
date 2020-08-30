package com.asianpaints.entities.converters


import androidx.room.TypeConverter
import com.asianpaints.core.Utility
import com.asianpaints.entities.model.idea.CollectionDecorModel


class CollectionDecorModelConverter {

    @TypeConverter
    fun stringToCollectionDecorModel(data: String?): CollectionDecorModel? {
        if (data == null) {
            return null
        }
        val gson = Utility.defaultGson()
        return gson.fromJson(data, CollectionDecorModel::class.java)
    }

    @TypeConverter
    fun collectionDecorModelToString(collectionDecorModel: CollectionDecorModel?): String? {
        if (collectionDecorModel == null) {
            return null
        }
        val gson = Utility.defaultGson()
        return gson.toJson(collectionDecorModel)
    }
}

package com.ochess.edict.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ochess.edict.data.local.entity.Label

class LabelConverter {

    @TypeConverter
    fun fromList(labels: List<Label>): String {
        return Gson().toJson(labels)
    }

    @TypeConverter
    fun fromJson(jsonString: String): List<Label> {
        return Gson().fromJson(jsonString, object: TypeToken<List<Label>>() {}.type)
    }
}
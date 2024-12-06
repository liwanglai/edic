package com.ochess.edict.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SynonymConverter {

    @TypeConverter
    fun fromList(synonym: List<String>): String {
        return Gson().toJson(synonym)
    }

    @TypeConverter
    fun fromJson(jsonString: String): List<String> {
        return Gson().fromJson(jsonString, object: TypeToken<List<String>>() {}.type)
    }
}
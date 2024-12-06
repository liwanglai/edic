package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.ochess.edict.domain.model.WordModel

data class DictionarySubEntity(
    @PrimaryKey
    @ColumnInfo(name = "wordsetId")
    val wordsetId: String,
    @ColumnInfo(name = "word")
    val word: String,
    val level: Int = 1
) {

    fun toWordModel(): WordModel {
        return WordModel(null, word, wordsetId, level, ch = null)
    }

    companion object{
        fun empty(): DictionarySubEntity = DictionarySubEntity("", "")
    }
}

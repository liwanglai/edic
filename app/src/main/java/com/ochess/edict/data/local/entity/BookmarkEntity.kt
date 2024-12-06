package com.ochess.edict.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ochess.edict.domain.model.WordModel

@Entity(tableName = "bookmarkTable")
data class BookmarkEntity(
    val meanings: List<Meaning>?,
    val word: String,
    @PrimaryKey
    val wordsetId: String,
    val level: Int = 1,
    val ch: String = "",
    val intime: Long = 0L
) {
    fun toWordModel(): WordModel {
        return WordModel(meanings, word, wordsetId, level, ch = ch)
    }
}
package com.ochess.edict.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ochess.edict.domain.model.WordModel
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "historyTable")
data class HistoryEntity(
    val meanings: List<Meaning>?,
    val word: String,
    @PrimaryKey
    val wordsetId: String,
    val level: Int = 0,
    val status: Int = 0,
    var time: Long = 0,
    var ch: String? = null
) {
    fun toWordModel(): WordModel {
        return WordModel(meanings, word, wordsetId, level, ch = ch,date= SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
            Date(time)
        ))
    }

}

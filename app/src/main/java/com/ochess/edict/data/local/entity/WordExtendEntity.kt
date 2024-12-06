package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "word_extend",
    indices = arrayOf(
        Index(value = arrayOf("intime")),
        Index("name",unique=true)
    )
)
data class WordExtendEntity(
    val name: String,
    val data: String,
    val inword: String,
    val extword: String,
    @ColumnInfo(name = "intime", defaultValue = "CURRENT_TIMESTAMP")
    val intime: Long=0L,
    val len: Int=0,
    val isize: Int=0,
    val esize: Int=0,
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
) {
}
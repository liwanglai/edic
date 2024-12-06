package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ochess.edict.domain.model.WordModel
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "category",
    indices = arrayOf(
        Index(value = arrayOf("intime","uptime","pid")),
        Index("name",unique=true)
    )
)
data class CategoryEntity(
    val name: String,
    @ColumnInfo(name = "uptime", defaultValue = "CURRENT_TIMESTAMP")
    val uptime: Long=0L,
    @ColumnInfo(name = "intime", defaultValue = "CURRENT_TIMESTAMP")
    val intime: Long=0L,
    val pid: Int=0,
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
) {

    @Ignore
    constructor() : this("")
    val upTime
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(uptime))
}
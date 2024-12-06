package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "test_history",
    indices = arrayOf(
        Index(value = arrayOf("intime","score","aid","name")),
    )
)
data class TestEntity(
    var content:String="",
    var name:String="",
    var aid: Int = 0,
    var score:Float =0f,
    @ColumnInfo(name = "intime", defaultValue = "CURRENT_TIMESTAMP")
    var intime:Long=0L,
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,
) {

    val inTime
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(intime))
}

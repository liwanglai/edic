package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "article",
    indices = arrayOf(
        Index(value = arrayOf("intime","uptime","cid","name")),
        Index(value=arrayOf("name","cid"),unique=true)
    )
)
data class ArticleEntity(
    var content:String="",
    var name:String="",
    var cid: Int = 0,
    @ColumnInfo(name = "uptime", defaultValue = "CURRENT_TIMESTAMP")
    var uptime:Long =0L,
    @ColumnInfo(name = "intime", defaultValue = "CURRENT_TIMESTAMP")
    var intime:Long=0L,
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,

    var inited: Boolean = false,  //初始化过 MP3文件生成
) {
    @Ignore
    constructor() : this("")
    fun findWords(text:String=""): List<String> {
        var rt = arrayListOf<String>()
        val ct = if(text.length>0) text else content
        val sections = ct.split(Regex("[\n\r]+")).map{ it.trim() }
        sections.forEach{
            if(! it.matches(Regex("[?\\.!;]+"))){
                val words = it.split(Regex("[,， ]+"))
                rt.addAll(words.filter { it.matches(Regex("^[A-Za-z`']+$"))})
            }
        }

        return rt
    }

    val upTime
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(uptime))
}

package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 登记实体类
 **/
//@Entity(tableName = "studentworld")
 class ChineseWordEntity() {
    //@PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "id")
    val id: Int = 0

    //@ColumnInfo(name = "word")
    var word: String? = null // 单词

    //@ColumnInfo(name = "phonetic")
    val phonetic: String? = null // 音标

    //@ColumnInfo(name = "mean")
    var mean: String? = null // 中文翻译

    //@ColumnInfo(name = "level")
    var level: String? = null // 等级值, 大于0
}

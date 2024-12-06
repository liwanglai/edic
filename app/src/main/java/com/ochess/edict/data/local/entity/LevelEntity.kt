package com.ochess.edict.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 登记实体类
 **/
@Entity(tableName = "levelTable")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String?, // 等级名字全称
    @ColumnInfo(name = "level")
    val level: Int? // 等级值, 大于0
)

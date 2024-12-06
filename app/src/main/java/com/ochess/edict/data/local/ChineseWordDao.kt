package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.local.entity.ChineseWordEntity
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.LevelEntity

interface ChineseWordDao {
    fun queryAllData(): MutableList<ChineseWordEntity>

}
package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.LevelEntity

@Dao
interface LevelDao {

    @Query("select * from levelTable")
    fun queryAllLevel(): MutableList<LevelEntity>

    @Query("select * from levelTable where level=:level limit 1")
    fun queryName(level:Int): LevelEntity
    @RawQuery
    fun select(query: SupportSQLiteQuery): Array<LevelEntity>

}
package com.ochess.edict.data.model

import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.local.entity.CategoryEntity

interface CanCommandSelectDao {
    @RawQuery
    suspend fun select(query: SupportSQLiteQuery): Array<Any>
}
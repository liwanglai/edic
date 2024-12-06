package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Category
import kotlinx.coroutines.flow.filter

@Dao
interface TestDao {
    @Insert
     fun add(a: TestEntity)
    @Update
     fun update(a: TestEntity)
     @Query("delete from test_history where id=:id")
     fun delete(id: Int)

    @RawQuery
     fun select(query: SupportSQLiteQuery): List<TestEntity>
}

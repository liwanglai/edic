package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(a: CategoryEntity)
    @Update
    fun update(a: CategoryEntity) :Int
    @RawQuery
    fun select(query: SupportSQLiteQuery): Array<CategoryEntity>
    @Query("select * from category where pid=:pid")
    fun selectByPid(pid: Int): List<CategoryEntity>

    fun select(pid: Int): List<CategoryEntity> {
        return selectByPid(pid)
    }

    @Query("delete from category where id=:id")
    fun delete(id: Int) :Int
    @Query("select id from category where 1=1 order by id desc limit 1")
    fun getLastId(): Int
}

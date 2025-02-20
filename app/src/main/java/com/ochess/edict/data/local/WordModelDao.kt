package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmarkEntity: BookmarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(historyEntity: HistoryEntity)

    @Query("SELECT * FROM bookmarkTable")
    fun getAllBookmark(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarkTable limit 500")
    fun getBookmark500(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarkTable where word=:word")
    fun findBookmarkByWord(word:String): Flow<BookmarkEntity>

    @Query("SELECT * FROM historyTable")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM historyTable WHERE time>=:start AND time<:end AND status=:s")
    fun getHistoryList(start: Long, end: Long, s: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM historyTable WHERE time>=:start AND time<:end")
    fun getHistoryList(start: Long, end: Long): Flow<List<HistoryEntity>>
    @RawQuery
    fun getHistoryList(query: SupportSQLiteQuery): List<HistoryEntity>
    @RawQuery
    fun count(query: SupportSQLiteQuery): Int

    @Delete
    fun deleteBookmark(bookmarkEntity: BookmarkEntity)

    @Delete
    fun deleteHistory(historyEntity: HistoryEntity)

}
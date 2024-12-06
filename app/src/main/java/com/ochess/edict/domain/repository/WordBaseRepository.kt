package com.ochess.edict.domain.repository

import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface WordBaseRepository {

    suspend fun insertBookmark(bookmarkEntity: BookmarkEntity)

    suspend fun insertHistory(historyEntity: HistoryEntity)

    suspend fun getAllBookmark(): Flow<List<BookmarkEntity>>

    suspend fun getAllHistory(): Flow<List<HistoryEntity>>

    suspend fun getHistoryToday(): Flow<List<HistoryEntity>>

    suspend fun getHistoryThisWeek(): Flow<List<HistoryEntity>>

    suspend fun getHistoryThisMonth(): Flow<List<HistoryEntity>>

    suspend fun getHistoryToday(s: Int): Flow<List<HistoryEntity>>

    suspend fun getHistoryThisWeek(s: Int): Flow<List<HistoryEntity>>

    suspend fun getHistoryThisMonth(s: Int): Flow<List<HistoryEntity>>

    suspend fun deleteBookmark(bookmarkEntity: BookmarkEntity)

    suspend fun deleteHistory(historyEntity: HistoryEntity)

}
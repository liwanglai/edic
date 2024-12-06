package com.ochess.edict.data.repository

import com.ochess.edict.data.local.WordModelDao
import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.domain.repository.WordBaseRepository
import com.ochess.edict.util.DateUtil
import com.ochess.edict.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WordRepository @Inject constructor(
    private val wordModelDao: WordModelDao
) : WordBaseRepository {

    override suspend fun insertBookmark(bookmarkEntity: BookmarkEntity) {
        wordModelDao.insertBookmark(bookmarkEntity)
    }

    override suspend fun insertHistory(historyEntity: HistoryEntity) {
        wordModelDao.insertHistory(historyEntity)
    }

    override suspend fun getAllBookmark(): Flow<List<BookmarkEntity>> =
        wordModelDao.getAllBookmark()

    override suspend fun getAllHistory(): Flow<List<HistoryEntity>> =
        wordModelDao.getAllHistory()

    override suspend fun deleteBookmark(bookmarkEntity: BookmarkEntity) {
        wordModelDao.deleteBookmark(bookmarkEntity)
    }

    override suspend fun deleteHistory(historyEntity: HistoryEntity) {
        wordModelDao.deleteHistory(historyEntity)
    }

    override suspend fun getHistoryToday(): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.today()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end)
    }

    override suspend fun getHistoryThisWeek(): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.thisWeek()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end)
    }

    override suspend fun getHistoryThisMonth(): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.thisMonth()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end)
    }

    override suspend fun getHistoryToday(s: Int): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.today()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end, s)
    }

    override suspend fun getHistoryThisWeek(s: Int): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.thisWeek()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end, s)
    }

    override suspend fun getHistoryThisMonth(s: Int): Flow<List<HistoryEntity>> {
        val timeStamp = DateUtil.thisMonth()
        return wordModelDao.getHistoryList(timeStamp.start, timeStamp.end, s)
    }
}
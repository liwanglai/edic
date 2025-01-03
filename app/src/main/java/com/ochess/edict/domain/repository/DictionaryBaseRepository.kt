package com.ochess.edict.domain.repository

import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.local.entity.LevelEntity
import com.ochess.edict.domain.model.WordModel
import kotlinx.coroutines.flow.Flow

interface DictionaryBaseRepository {
    fun search(word: String): Flow<List<DictionaryEntity>>
    fun size(): Int
    fun searchByText(text: String): Flow<DictionaryEntity>

    fun searchByWordSetId(text: String, wordSetId: String): Flow<DictionaryEntity>

    fun prefixMatch(word: String): Flow<List<DictionaryEntity>>

    fun getAllLevel(): Flow<List<LevelEntity>>

    fun getWordSetInCacheByLevel(level: Int): Flow<List<DictionarySubEntity>>

    fun randomCacheSubEntityIdx(): Int

    fun getCacheSubEntity(idx: Int): DictionarySubEntity?

    // fun removeCacheSubEntityAt(idx: Int): DictionarySubEntity?

    fun clearCacheSubEntity()
    fun getWorkIndex(index: Int=0): Int

    fun getWords():List<WordModel>
    fun setWords(wList: List<WordModel>)
    fun searchByCh(query: String): Flow<List<DictionaryEntity>>
}
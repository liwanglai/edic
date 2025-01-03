package com.ochess.edict.data.repository

import android.database.sqlite.SQLiteException
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.sqlite.db.SimpleSQLiteQuery
import com.ochess.edict.data.local.ChineseWordDao
import com.ochess.edict.data.local.DictionaryDao
import com.ochess.edict.data.local.LevelDao
import com.ochess.edict.data.local.WordModelDao
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.local.entity.LevelEntity
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.domain.repository.DictionaryBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.LinkedHashMap
import java.util.LinkedList
import java.util.Random
import javax.inject.Inject

class DictionaryRepository @Inject constructor(
    private val dictionaryDao: DictionaryDao,
    private val levelDao: LevelDao,
    private val wordModelDao: WordModelDao
) : DictionaryBaseRepository {

    // 缓存当前Level的SubEntity, 对此数据结构更改要加锁
    private val subDictionaryList = LinkedList<DictionarySubEntity>()
    private val subDictionaryIndexList = ArrayList<Int>()
    private val subDictionaryIndexMap = LinkedHashMap<String, Int>()
    private var oldCacheLevel = -1
    var workIndex = 0

    @Synchronized
    override fun randomCacheSubEntityIdx(): Int {
        if (subDictionaryIndexList.isNotEmpty()) {
            if(subDictionaryIndexList.size>1)
                return kotlin.random.Random.nextInt(0, subDictionaryIndexList.size - 1)
            else return 0
        }
        return -1
    }

    @Synchronized
    override fun getCacheSubEntity(idx: Int): DictionarySubEntity {
        workIndex = subDictionaryIndexList[idx]
        subDictionaryIndexList.removeAt(idx)
        return subDictionaryList[workIndex]
    }

    @Synchronized
    override fun clearCacheSubEntity() {
        subDictionaryIndexList.clear()
        subDictionaryIndexMap.clear()
        subDictionaryList.clear()
    }

    override fun prefixMatch(word: String): Flow<List<DictionaryEntity>> = flow {
        if (validateChar(word)) {
            val query = """
                SELECT * FROM ${word.first()}_table WHERE word LIKE ? ORDER BY word ASC LIMIT 20
            """
            val queryObj = SimpleSQLiteQuery(query, arrayOf("${word}%"))
            emit(dictionaryDao.prefixMatch(queryObj))
        }
    }

    override fun search(word: String): Flow<List<DictionaryEntity>> = flow {
        if (validateChar(word)) {
            val query = """
                SELECT * FROM ${word.first()}_table WHERE word = ?
            """
            val queryObj = SimpleSQLiteQuery(query, arrayOf(word))
            emit(listOf(dictionaryDao.search(queryObj)))
        }
    }

    override fun getAllLevel(): Flow<List<LevelEntity>> = flow {
        emit(levelDao.queryAllLevel())
    }

    @Synchronized
    override fun getWordSetInCacheByLevel(level: Int): Flow<List<DictionarySubEntity>> = flow {
        if (oldCacheLevel == level && level > -1) {
            emit(subDictionaryList)
        }
        subDictionaryList.clear()
        subDictionaryIndexMap.clear()
        subDictionaryIndexList.clear()
        var c = 'a'
        var query = ""
        val historyList = wordModelDao.getAllHistory().firstOrNull()
        while (c <= 'z') {
            query = """
                SELECT t.wordsetId,t.word FROM ${c}_table as t WHERE level = ?
            """
            try {
                val queryObj = SimpleSQLiteQuery(query, arrayOf(level))
                val subList = dictionaryDao.getDictionarySubEntity(queryObj)
                subDictionaryList.addAll(subList)
            } catch (ex: SQLiteException) {
                ex.printStackTrace()
            }
            c++
        }
        if (historyList?.isNotEmpty() == true) {
            subDictionaryList.forEachIndexed { index, dictionarySubEntity ->
                subDictionaryIndexMap[dictionarySubEntity.word] = index
            }
            historyList.forEach {
                subDictionaryIndexMap.remove(it.word)
            }
        } else {
            subDictionaryList.forEachIndexed { index, dictionarySubEntity ->
                subDictionaryIndexMap[dictionarySubEntity.word] = index
            }
        }
        subDictionaryIndexList.addAll(subDictionaryIndexMap.values)
        oldCacheLevel = level
        emit(subDictionaryList)
    }
    override fun size():Int{
        return subDictionaryList.size
    }
    override fun getWords():List<WordModel>{
        return subDictionaryList.map{it.toWordModel()}
    }
    override fun setWords(wList:List<WordModel>){
        clearCacheSubEntity()
        subDictionaryList.addAll(wList.map{ DictionarySubEntity(it.wordsetId,it.word,it.level) })
        subDictionaryList.forEachIndexed { index, dictionarySubEntity ->
            subDictionaryIndexMap[dictionarySubEntity.word] = index
        }
        subDictionaryIndexList.addAll(subDictionaryIndexMap.values)
    }

    override fun getWorkIndex(index: Int): Int {
        if(index>0) {
           return subDictionaryIndexList[index]
        }else if(index<0) {
            var word = subDictionaryList[-index].word
            if(subDictionaryIndexMap.get(word) != null) {
                return subDictionaryIndexList.indexOf(-index)
            }
        }
        return  workIndex
    }
    override fun searchByText(text: String): Flow<DictionaryEntity> = flow {
        if (validateChar(text)) {
            val query = """
                SELECT * FROM ${text.first()}_table WHERE word = ?
            """
            val queryObj = SimpleSQLiteQuery(query, arrayOf(text))
            emit(dictionaryDao.search(queryObj))
        }
    }

    override fun searchByCh(text: String): Flow<List<DictionaryEntity>> = flow {
            val words = arrayListOf<DictionaryEntity>()
            ('a'..'z').asFlow().onEach {
                val query = """
                    SELECT * FROM ${it}_table WHERE ch like '%${text}%' limit 10
                """
                val queryObj = SimpleSQLiteQuery(query)
                val finds = dictionaryDao.prefixMatch(queryObj)
                if(finds.size>0) {
                    words.addAll(finds)
                }
            }.onCompletion {
                emit(words)
            }.collect{}
    }

    override fun searchByWordSetId(text: String, wordSetId: String): Flow<DictionaryEntity> = flow {
        val query = """
                SELECT * FROM ${text.first()}_table WHERE wordsetId = ?
            """
        val queryObj = SimpleSQLiteQuery(query, arrayOf(text))
        emit(dictionaryDao.search(queryObj))
    }
}

private fun validateChar(word: String) : Boolean {
    if (word.isEmpty()) return false
    val firstChar = word.first().lowercase().toCharArray()[0]
    val isValidLetter = Character.isLetter(firstChar)
    val inASCII = firstChar in 'a'..'z'
    return isValidLetter && inASCII
}
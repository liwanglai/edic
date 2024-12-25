package com.ochess.edict.data.local

import androidx.collection.arrayMapOf
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.local.entity.WordExtendEntity
import com.ochess.edict.domain.model.WordModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@Dao
interface DictionaryDao {
    @RawQuery
    fun prefixMatch(query: SupportSQLiteQuery): List<DictionaryEntity>

    @RawQuery
    fun search(query: SupportSQLiteQuery): DictionaryEntity

    @RawQuery
    fun getDictionarySubEntity(query: SupportSQLiteQuery): List<DictionarySubEntity>

    fun wordIn(table:String, words: ArrayList<String>): List<DictionaryEntity> {
        var args = words.map{"?"}.joinToString(",")
        val sql = String.format("select * from ${table} where word in(${args})")
        val queryObj = SimpleSQLiteQuery(sql, words.toArray())
        return prefixMatch(queryObj)
    }
    fun search(words: List<String>): List<DictionaryEntity> {
        val rt = arrayListOf<DictionaryEntity>()
        val map = arrayMapOf<Char,ArrayList<String>>()
         val cAll = "a".."z"
        words.forEach{
            val c = it[0].toLowerCase()
            if(c.toString() in cAll) {
                if (map[c] == null) {
                    map[c] = arrayListOf()
                }
                map[c]?.add(it)
            }
        }
        runBlocking {
//            map.map {
//                async {
//                    val words = wordIn(it.key + "_table", it.value)
//                    rt.addAll(words)
//                }
//            }.forEach { it.join() }
            flow {
                map.forEach {
                    val words = wordIn(it.key + "_table", it.value)
                    emit(words)
                }
            }.collect {
                rt.addAll(it)
            }
        }
         if(!PageConf.getBoolean(PageConf.homePage.SortWords,true)){
             val wordMap = rt.groupBy { it.word }
             rt.clear()
             words.map{
                 if(it in wordMap.keys) {
                     rt.add(wordMap.get(it)!!.first())
                 }else{
                     rt.add(DictionaryEntity("[]",it,"","",0))
                 }
             }
         }

        return rt
    }

    @RawQuery
    fun getWordExtend(query: SupportSQLiteQuery): List<WordExtendEntity>
    fun find(word: String): WordModel?{
        val rows = wordIn(word.first()+"_table", arrayListOf(word))
        if(rows.size==0) return null
        return rows.first().toWordModel()
    }

}
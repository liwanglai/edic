package com.ochess.edict.data.model

import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.Db
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 单词扩展 按照这个单词作为词根
 */
@HiltViewModel
class WordExtend @Inject constructor() : Model() {

    var word: String=""
    var name: String = ""
    var esize: Int =0
    var isize: Int =0
    var data = listOf<String>()
    var inword = listOf<String>()
    var extword = listOf<String>()
    constructor(word: String) : this() {
        this.word = word
        name = getname(word)
        val q = self.query.where("name", name).build()
        val wee = self.waitLaunch({
            dao.getWordExtend(q)
        })
        if(wee==null || wee.size==0) return
        val w = wee[0]
        esize = w.esize
        isize = w.isize
        if(w.inword.length>0) {
            inword = w.inword.split(",")
        }
        if(w.extword.length>0) {
            extword = w.extword.split(",").filter { it.length>0 }
        }
        data = w.data.split(",")
    }

    companion object {
        val dao = Db.dictionary.dictionaryDao
        val self = WordExtend()
        fun getname(word:String): String {
            val sets = hashSetOf<Char>()
            word.forEach {
                sets.add(it)
            }
            val a = sets.map { it }
            Collections.sort(a)
            return a.joinToString("")
        }
    }

    fun <T>waitLaunch(run: suspend CoroutineScope.() -> T?, ws:Long = 500) : T{
        val countDownLatch = CountDownLatch(1)
        var rt:T? = null
        viewModelScope.launch(Dispatchers.IO) {
            rt = run()
            countDownLatch.countDown()
        }
        countDownLatch.await(ws, TimeUnit.MILLISECONDS)
        return  rt as T
    }

    fun eSize(): Int {
        val num = extword.filter{it.matches(Regex(word))}
        return num.size
    }
}
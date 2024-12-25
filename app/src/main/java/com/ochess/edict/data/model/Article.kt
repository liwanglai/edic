package com.ochess.edict.data.model

import androidx.collection.arraySetOf
import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.domain.model.WordModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
    class Article @Inject constructor() : Model()  {
    companion object {
        val dao = Db.user.article
        val self= Article()
        fun add(value: String, name: String, cid:Int=0) {
            val t = System.currentTimeMillis()
            self.save(ArticleEntity(value, name, cid,t,t))
        }

        fun getByCid(
            cid: Int,
            callbak:(List<ArticleEntity>)->Unit={}
        ): MutableStateFlow<List<ArticleEntity>> {
            val aList = MutableStateFlow(listOf<ArticleEntity>())
            self.searchByCid(cid){
                aList.value = it
                callbak(it)
            }
            return aList
        }

        fun getContent(id: Int, onGet: (ct:String) -> Unit) {
            self.exec{
                val a = dao.find(id)
                onGet(a.content)
            }
        }

        fun delete(id: Int) {
//            self.exec{
//                dao.delete(id = id)
//            }
            self.delete(id)
        }

        fun finds(aids: List<Int>, hasContent: Boolean=false,f: (a: Array<ArticleEntity>) -> Unit) {
            val query = self.query.whereIn("id",aids)
            if(!hasContent) query.select("name,cid,uptime,intime,id,'' as content")
            self.launch{
                val a = dao.select(query.build())
                f(a)
            }
        }

        fun update(art : ArticleEntity) {
              self.save(art)
        }

        fun findByCids(cids: List<Int>, hasContent: Boolean, f: (a: Array<ArticleEntity>) -> Unit) {
            val query = self.query.whereIn("cid",cids)
            if(!hasContent) query.select("name,cid,uptime,intime,id,'' as content")
            val queryObj = query.build()
            self.launch{
                val a = dao.select(queryObj)
                f(a)
            }
        }

        fun grep(s: String,q:Query.()->Unit={}): Array<ArticleEntity> {
            val query = self.query.like("content",s)
            query.select("name,cid,uptime,intime,id,'' as content")
            q.invoke(query)
            val queryObj = query.build()
            return dao.select(queryObj)
        }

        /**
         * 通过单词获取对象
         */
        fun getWords(words: List<String>): List<WordModel> =runBlocking {
            val rt =Db.dictionary.dictionaryDao.search(words.map { it.trim() })
            rt.map{it.toWordModel()}
        }
        fun getWords(words: List<String>,f:(a:List<WordModel>) -> Unit) {
            self.launch {
                val sHave = arraySetOf<String>()
                val a = Db.dictionary.dictionaryDao.search(words.map{it.trim()}).map {
                    sHave.add(it.word)
                    it.toWordModel()
                }
                val rt = arrayListOf<WordModel>()
                rt.addAll(a)
                words.subtract(sHave).forEach{
                    rt.add(WordModel(null,it,"",0,0,"",""))
                }
                f(rt)
            }
        }

        fun addFile(data: String, f: String) {
            dao.addFile(data,f)
        }

        fun isEmpty(): Boolean {
            return  dao.select(self.query.limit(1).build()).size ==0
        }
        fun find(id: Int): ArticleEntity? {
            return  self.waitLaunch({
                dao.find(id)
            })
        }
    }

    fun <T>waitLaunch(run: suspend CoroutineScope.() -> T?,ws:Long = 500) : T{
        val countDownLatch = CountDownLatch(1)
        var rt:T? = null
        viewModelScope.launch(Dispatchers.IO) {
             rt = run()
            countDownLatch.countDown()
        }
        countDownLatch.await(ws, TimeUnit.MILLISECONDS)
        return  rt as T
    }
    private fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

    private fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(id)
        }
    }

    private fun save(articleEntity: ArticleEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if(articleEntity.id==0) {
                dao.add(articleEntity)
            }else{
                dao.update(articleEntity)
            }
        }
    }

    private fun searchByCid(cid: Int, onGet: (List<ArticleEntity>) -> Unit)  {
        viewModelScope.launch(Dispatchers.IO) {
            onGet(dao.search(cid=cid))
        }
    }
}
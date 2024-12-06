package com.ochess.edict.data.model

import androidx.collection.arrayMapOf
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class Category  @Inject constructor() : Model() {
    companion object {
        val dao = Db.user.category
        val self = Category()
        fun getByPid(pid:Int,callbak:(List<CategoryEntity>)->Unit={}): MutableStateFlow<List<CategoryEntity>> {
            val rt = MutableStateFlow(listOf<CategoryEntity>())
            self.select(pid){
                rt.value=it
                callbak(it)
            }
            return rt
        }

        fun add(name: String, pid: Int) {
            val t = System.currentTimeMillis()
            self.save(CategoryEntity(name,t,t,pid=pid))
        }

        fun getParents(pid: Int): ArrayList<CategoryEntity> {
            if(pid< 0) {
                return arrayListOf(CategoryEntity(VirtualCommonItem.get(pid)!!.name))
            }
            var rt = arrayListOf<CategoryEntity>()
            val queryObj = self.getQuery().where("id","<=",pid).build()
            val all = self.select(queryObj)
            var pMap = arrayMapOf<Int,CategoryEntity>()
            all.forEach{
                pMap[it.id] = it
            }
            if(pMap[pid]!=null) {
                val nowMenu = pMap[pid]!!
                rt.add(nowMenu)
                var id = nowMenu.pid
                while (id > 0) {
                    val p = pMap[id]!!
                    rt.add(p)
                    id = p.pid
                }
            }
            return rt
        }

        fun getAllCids(id:Int): List<Int> {
            var rt = arrayListOf(CategoryEntity("",id=id))
            var qo = self.getQuery().where("pid",id).build()
            var nextDo = true
            do {
                val a = self.select(qo)
                rt.addAll(a)
                val ids = a.map{it.id}
                qo = self.getQuery().whereIn("pid",ids).build()
                nextDo = a.size>0
            }while(nextDo)
            val cids = rt.map{it.id}
            return cids
        }
        fun getArticles(id: Int,hasContent:Boolean=false, f:(a:Array<ArticleEntity>)->Unit) {
            val cids = getAllCids(id)
            Article.findByCids(cids,hasContent,f)
        }

        fun delete(id: Int) {
            self.delete(id)
        }

        fun update(categoryEntity: CategoryEntity) {
            self.save(categoryEntity)
        }
    }
    fun delete(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(id)
        }
    }
    private fun select(q: SupportSQLiteQuery): Array<CategoryEntity> {
        var rt = arrayOf<CategoryEntity>()
        val countDownLatch = CountDownLatch(1)
        viewModelScope.launch(Dispatchers.IO) {
            rt = dao.select(q)
            countDownLatch.countDown()
        }
        countDownLatch.await(500, TimeUnit.MILLISECONDS)
        return rt
    }

    private fun select(pid: Int=-1, onGet: (List<CategoryEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            onGet(dao.select(pid=pid))
        }
    }

    fun save(categoryEntity: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if(categoryEntity.id>0) {
                dao.update(categoryEntity)
            }else {
                dao.add(categoryEntity)
            }
        }
    }
}
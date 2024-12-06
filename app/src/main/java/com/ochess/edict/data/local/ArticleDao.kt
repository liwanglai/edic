package com.ochess.edict.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Category
import kotlinx.coroutines.flow.filter

@Dao
interface ArticleDao {
    @Insert
     fun add(a: ArticleEntity)
    @Update
     fun update(a: ArticleEntity)
    @RawQuery
     fun select(query: SupportSQLiteQuery): Array<ArticleEntity>

    @Query("select * from article where id = :id")
    fun find(id:Int) :ArticleEntity

    @Query("select name,id,uptime,cid,intime,inited,'' as content from article where cid = :cid")
    fun findByCid(cid:Int) :List<ArticleEntity>

     fun search(cid: Int=0,name:String =""):List<ArticleEntity>{
        return findByCid(cid)
//        return arrayListOf(ArticleEntity(name="aa"),ArticleEntity(name="bb"))
    }

     fun findWords(id: Int): List<DictionaryEntity> {
        val words = find(id).findWords()
        return Db.dictionary.dictionaryDao.search(words)
    }

    @Query("delete from article where id=:id")
     fun delete(id:Int)
    @Query("select * from article where id in (:ids)")
     fun finds(ids: String): Array<ArticleEntity>
    fun addFile(data: String, f: String){
        val cg = Db.user.category
        var all = f.split(Regex("/"))
        val file = all.last()
        all = all.subList(0,all.size-1)
        var pid = 0
        for(pname in all) {
            val cs = cg.selectByPid(pid)
            val ctg = cs.filter { it.name.equals(pname) }
            if(ctg.size==0){
                val cgm = CategoryEntity(pname,pid=pid)
                cg.add(a = cgm)
                pid = cg.getLastId()
            }else{
                pid = ctg[0].id
            }
        }
        add(ArticleEntity(data,file,pid))
    }
}

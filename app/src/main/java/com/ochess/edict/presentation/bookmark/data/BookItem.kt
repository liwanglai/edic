package com.ochess.edict.presentation.bookmark.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Category
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.main.components.Confirm


enum class BookItemType{
    article,
    category,
    word
}
data class BookItem(
    @JsonProperty("name")
    var name:String,
    @JsonProperty("id")
    var id:Int=0,
    @JsonProperty("intime")
    var intime:Long=0L,
    @JsonProperty("uptime")
    var uptime:Long=0L,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("pid")
    var pid:Int=0,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("cid")
    var cid:Int=0,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("content")
    var content:String?=null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("upTime")
    var upTime:String?="",
){
    companion object{
        fun build(item:CategoryEntity): BookItem {
            val rt=  ObjectMapper().convertValue(item, BookItem::class.java)
            rt.type =  BookItemType.category
            rt.ico = R.drawable.category
            return rt
        }
        fun build(item:ArticleEntity): BookItem {
            val rt=  ObjectMapper().convertValue(item, BookItem::class.java)
            rt.type = BookItemType.article
            rt.ico = R.drawable.article
//            rt.ico = R.drawable.books
            rt.pid = rt.cid
            return rt
        }

        fun build(item: WordModel): BookItem {
            var rt = BookItem(item)
            return rt
        }
    }
    constructor(m: Int) : this(""){
        name = "分类"
        id = 1
        ico = R.drawable.category
        type = BookItemType.category
    }
    constructor(m: WordModel) : this(""){
        name = m.word
        content = m.ch
    }

    var inited: Boolean by mutableStateOf(true)
    var initStatusText: String by mutableStateOf("待下载")
    var ico: Int=0
    var type:BookItemType = BookItemType.word
    var isSelect:Boolean by mutableStateOf(false)
    var index=0
    fun changeSelecte() {
        this.isSelect = !this.isSelect
        BookMarkEvent.ItemOnSlectChange(this)
    }

    fun delete() {
        when(this.type){
            BookItemType.category ->
                //查找一下目录是否为空
                Category.getArticles(this.id){
                    if(it.size > 0){
                        Confirm.show("目录不为空确定要删除吗?"){
                            Category.delete(this.id)
                        }
                    }else{
                        Category.delete(this.id)
                    }
                }
            BookItemType.article ->
                Article.delete(this.id)
            BookItemType.word -> {
                val words = GlobalVal.bookmarkViewModel.bookmarks.value
                val word = words.filter { it.word.equals(this.name) }[0]
                GlobalVal.bookmarkViewModel.deleteBookmark(word)
            }
        }
    }

    fun save(content: String="",name:String="",pid:Int=0,add:ArrayList<BookItem>?=null) {
        if(content.length>0) this.content = content
        if(name.length>0) this.name = name
        if(pid>0) this.pid = pid
        when(type){
            BookItemType.category ->
                Category.update(CategoryEntity(this.name,System.currentTimeMillis(),this.intime,this.pid,this.id))
            BookItemType.article -> {
                if(this.content!!.length==0){
                    Article.getContent(this.id){
                        this.content = it
                        if(add!=null){
                            this.content= add.map{it.name}.joinToString ("\n")+"\n"+this.content
                        }
                        Article.update( ArticleEntity(this.content!!,this.name,this.pid,System.currentTimeMillis(),this.intime,this.id,this.inited))
                    }
                }else{
                    Article.update( ArticleEntity(this.content!!,this.name,this.pid,System.currentTimeMillis(),this.intime,this.id,this.inited))
                }

            }
            else -> {

            }
        }

    }

    fun info(): String {
        return ""
    }

}
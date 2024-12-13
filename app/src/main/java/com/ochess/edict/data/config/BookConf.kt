package com.ochess.edict.data.config

import android.os.Handler
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.collection.arrayMapOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.WordExtend
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.presentation.history.BookHistroy
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.view.skin.LayoutJdc.Views.Adapter
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Collections
import java.util.Locale
import java.util.Random

data class BookConf(
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("id")
    val id: Int=0,
    @JsonProperty("intime")
    val intime: Long=0,
    @JsonProperty("chapterName")
    var chapterName: String = "",

) {

    @JsonIgnore
    var index:Int = 0;
    @JsonIgnore
    var size:Int = 0;
    @JsonIgnore
    var word:String = "";
    @JsonIgnore
    var ch:String = "";
    @JsonIgnore
    var lastBookId =0
    @JsonIgnore
    var eventNextDone: (() -> Unit?)? = null
    @JsonIgnore
    public lateinit var wordMode: WordModel
    companion object {
        val openedBook = mutableStateOf("")
        val mPid = -2

        var instance:BookConf = usBook()
        //user status 存储的book
        fun usBook(v:BookConf?=null): BookConf {
            if(v!=null) {
                UserStatus().setData("BookConf",v)
                return v
            }
            val rt = UserStatus().getData("BookConf", BookConf::class.java) ?:BookConf()
            var obn = rt.name.replace(Regex("\\.\\w+$"),"")
                if(obn.length>4) obn=obn.substring(0,4)+".."
            openedBook.value = obn
            return rt
        }

        //下载课本
        fun downLoad(file: String, name: String) {
            FileUtil.getData(file) {
                Article.add(it, name, VirtualCommonItem.getId(VirtualCommonItem.Type.donwload))
            }
        }
        /**
         * 设置book
         * bookConf 要设置的配置
         * saveDo   是不是保存配置
         */
        fun setBook(bookConf: BookConf?=null,saveDo:Boolean = false) {
            instance = if(bookConf==null) usBook() else bookConf
            if(saveDo) {
                usBook(instance)
            }
        }

        fun selectBook() {
//            NavScreen.BookmarkScreen.open("?pid=-2")
            NavScreen.openJdc(-1)
        }

        fun getHaveBooks(layoutId:Int,onBindViewHolder:(ViewGroup,BookConf)->Unit ): Adapter {
            var ap = Adapter(ActivityRun.context(),layoutId,{it,vItem,pos->
                onBindViewHolder(it,vItem as BookConf)
            })
            Handler().post {
                getHaveBooks {
                    ap.setItems(it as ArrayList<Any>)
                }
            }

            return ap
        }
        fun getHaveBooks(onGet:(bs:ArrayList<BookConf>)->Unit) {
            Article.getByCid(mPid){
                var clist = arrayListOf<BookConf>()
                clist.add(BookConf("",0,0))
                it.forEach {
                    clist.add(BookConf(it.name,it.id,it.intime))
                }
                onGet(clist)
            }
        }

        fun getChapterWords(layoutId:Int,onBindViewHolder:(ViewGroup,WordModel)->Unit ): ListAdapter {
            var ap = Adapter(ActivityRun.context(),layoutId,{it,vItem,pos->
                onBindViewHolder(it,vItem as WordModel)
            })
            if(words.size>0) {
                ap.setItems(words as ArrayList<Any>)
            }
            return ap
        }
        val upStatus: MutableStateFlow<Int> = MutableStateFlow(0)
        var doc = ""
        var chapters = arrayListOf<String>()
        var chapterMapWords = arrayMapOf<String,List<String>>()
        var words by mutableStateOf(listOf<WordModel>())
    }

    fun initArticle()  {
        if(lastBookId== instance.id && instance.size>0){
//            index=0
//            next(0)
            return
        }
        lastBookId = instance.id
        Article.finds(listOf( instance.id),true){
            if(it.size==0) return@finds
            val a = it.first()
            doc = a.content
            initByData(doc)
            //usBook(this)
        }
    }
    fun initByData(doc:String){
        chapters.clear()
        chapterMapWords.clear()
        var beforeLine = ""
        var beforeChapter = ""
        var mapWords = ""
        doc.split(Regex("[\\r\\n]")).forEach{
            if(beforeLine.length ==0 && it.length>0) {
                if(mapWords.length>0) {
                    chapterMapWords[beforeChapter] = mapWords.split(Regex(",|，")).map{it.trim()}.filter { it.length>0 }//a.findWords(mapWords)
                    mapWords = ""
                }
                chapters.add(it)
                beforeChapter = it
            }else{
                mapWords += ",$it"
            }
            beforeLine = it
        }
        if(mapWords.length>0) {
            chapterMapWords[beforeChapter] = mapWords.split(Regex(",|，")).map{it.trim()}.filter{it.length>0}
        }
        if(chapters.size ==1){
            val first = chapters[0]
            chapters[0] = "defaultChapter"
            val newList = arrayListOf(first)
                newList.addAll(chapterMapWords[first]!!)
            chapterMapWords[chapters[0]] = newList
            chapterMapWords.remove(first)
        }
        if(!chapters.contains(chapterName)) {
            chapterName = chapters[0]
        }
        save(chapterName,false)

    }

    fun save(chapterName: String="",doSave:Boolean=true) {
        //文章中没有此数据就不要保存防止下一次进来没数据
        if(chapterName!="defaultChapter" && doc.indexOf(chapterName) == -1) {
            return
        }
        if(chapterName.length>0) {
            this.chapterName = mt(chapterName)
        }
        var mWords = chapterMapWords[chapterName]
        if(mWords!=null) {
            Article.getWords(mWords) {
                words = it
                GlobalVal.wordModelList = words
                val word = BookHistroy.lastWord()
                if(word.length>0) {
                    setWordByString(word)
                }
                GlobalVal.wordViewModel.upList(GlobalVal.wordModelList)
                next(0)
            }
        }

        if(doSave) {
            usBook(this)
        }
        upStatus.value++
    }

    fun next(d:Boolean):Boolean {
        val map = arrayMapOf(true to 1,false to -1)
        return next(map[d]!!)
    }
    fun onNextDone(function: () -> Unit) {
        eventNextDone = function
    }
    fun next(n:Int=1):Boolean {
        if(n==1 && !HomeEvents.onNextWordBefore()){
            return false
        }
        this.index+=n
        if(words.size==0 || index>= words.size || index<0) return false
        val word  = words[index]
        this.size = words.size
        next(word)
        if(index<size && index>=0) {
            return true
        }
        return false
    }

    fun next(word: WordModel){
        this.word = word.word
        wordMode = word
        if(word!=null && word.ch!=null) {
            this.ch = word.ch!!
        }
        GlobalVal.wordViewModel.setWord(DictionarySubEntity(word.wordsetId,word.word,word.level))
        if(eventNextDone!=null) {
            eventNextDone!!()
        }
        //保存单词进度
        if(index>0) {
            BookHistroy.lastWord(word.word)
        }


    }
    fun pic(pGet: (u: String?) -> Unit) {
        //var img = "https://cn.bing.com/images/search?q=+"+word
        //img = "https://tse2-mm.cn.bing.net/th/id/OIP-C.31S79ct-_5zHuUbVBUtfLwHaHa"
        var imgFile = PathConf.imgs+word+".jpg"
        if(FileUtil.exists(imgFile)) {
            pGet(imgFile)
        }else{
            pGet(null)
        }
    }

    fun getInputKeys(isUpper:Boolean=true,count:Int=0): List<Char> {
        val a = hashSetOf<Char>()
        var w = if(isUpper)word.toUpperCase() else word.toLowerCase()
        w.toList().forEach { a.add(it) }
        val noused = getRandChar(a.joinToString(),isUpper)
        val add = if(count==0) 3 else count - a.size
        val start = Random().nextInt(noused.length-add)
        noused.substring(start,start+add).toList().forEach {
            a.add(it)
        }
        var rt = a.toList()
        Collections.shuffle(rt)
        return rt
    }


    fun getRandChar(usedchars:String,isUpperCase:Boolean=true): String {
        var nousedChars=""

        var charAll = "abcdefghijklmnopqrstuvwxyz"
        if (isUpperCase) {
            charAll = charAll.uppercase(Locale.getDefault())
        }
        val has = charAll.toCharArray()

        for (i in 0..has.size-1) {
            if (usedchars.indexOf(has[i])==-1) nousedChars += has[i]
        }

        return nousedChars
    }

    fun setWordByString(word:String) {
        this.word = word
        index = words.map{it.word}.indexOf(word)
        next(0)
    }
    fun setWord(it: WordModel) {
        index = words.map{it.word}.indexOf(it.word)
        next(0)
    }

    fun nowBook(): BookItem {
        val article = Article.find(id)
        return  BookItem.build(article!!)
    }

    fun setContent(s: String, hashData: LinkedHashMap<String, List<String>>) {
        name = s.replace(regex = Regex("\\.\\w+$"),"")
        val rows=hashData.map {
            it.key +"\n"+ it.value.joinToString ( "," )
        }.joinToString ("\n\n" )
        initByData(rows)
    }

    fun cid(): Int {
        if(id ==0) return 0
        val article = Article.find(id)
        return article?.cid ?: 0
    }

    fun wordEx(): WordExtend {
        return WordExtend(word)
    }

}

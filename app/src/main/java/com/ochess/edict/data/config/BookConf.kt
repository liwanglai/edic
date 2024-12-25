package com.ochess.edict.data.config

import android.os.Handler
import androidx.collection.arrayMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.WordExtend
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.BookHistroy
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.main.extend.MainRun
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.presentation.main.extend.setTimeout
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.max

data class BookConf (
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
    public var wordMode: WordModel? =null
    class Events{
        var onWordChange:BookConf.(word:WordModel)->Unit = {}
        var onChaptersChange:BookConf.()->Unit = {}
        var onBookChange:BookConf.()->Unit = {}
    }
    companion object {
        val  TAG = "Book"
        val openedBook = mutableStateOf("")
        val mPid = -2

        lateinit var instance:BookConf
        val event = Events()
        val upStatus: MutableStateFlow<Int> = MutableStateFlow(0)
        var doc = ""
        var chapters = arrayListOf<String>()

        var chapterMapWords = arrayMapOf<String,List<String>>()
        var words by mutableStateOf(listOf<WordModel>())
        var chapterWordModels = listOf<WordModel>()
        var inited = false
        fun userBook(v: BookConf?=null): BookConf {
            if(v!=null) {
                UserStatus().setData("BookConf",v)
                return v
            }
            val rt = UserStatus().getData("BookConf", BookConf::class.java) ?: BookConf()
            return rt
        }
        public fun setBook(id:Int) {
            val aModel = Article.find(id)
            if(aModel!=null) {
                val nowBook = BookConf(aModel.name, aModel.id, aModel.intime)
                setBook(nowBook,true)
            }
        }
        /**
         * 设置book
         * bookConf 要设置的配置
         * saveDo   是不是保存配置
         */
        public fun setBook(bookConf: BookConf?=null, saveDo:Boolean = false) {
            instance = if(bookConf==null) userBook() else bookConf
            if(saveDo) {
                userBook(instance)
            }
            onOpenBook(instance)
        }
        private fun onOpenBook(bc: BookConf) {
            var obn = bc.name.replace(Regex("\\.\\w+$"),"")
            if(obn.length>4) obn=obn.substring(0,4)+".."
            openedBook.value = obn
            event.onBookChange.invoke(instance)
        }

        fun selectBook() {
            NavScreen.openJdc(-1)
        }
        val onBookChange  = {f:BookConf.()->Unit ->  event.onBookChange =f }
        val onChaptersChange  : (BookConf.()->Unit) -> Unit = { event.onChaptersChange = it }
        fun onWordChange (f:BookConf.(WordModel)->Unit) { event.onWordChange =f}
        fun initOnce() {
            if(!inited) {
                instance = userBook()
                onOpenBook(instance)
                inited=true
            }
        }
    }
    fun initArticle()  {
        if(lastBookId== instance.id && instance.size>0){
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
    private fun initByData(doc:String){
        var beforeLine = ""
        var beforeChapter = ""
        var mapWords = ""
        words = listOf<WordModel>()
        chapters.clear()
        chapterMapWords.clear()
        index=0
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
        if(chapters.size ==1 && name!="历史记录页"){
            val first = chapters[0]
            chapters[0] = "defaultChapter"
            val newList = arrayListOf<String>()
            first.split(Regex(",|，")).map{it.trim()}.filter { it.length>0 }.filter { newList.add(it) }
            if(chapterMapWords.size>0) {
                newList.addAll(chapterMapWords[first]!!)
            }
            chapterMapWords[chapters[0]] = newList
            chapterMapWords.remove(first)
        }
        if(!chapters.contains(chapterName)) {
            chapterName = chapters[0]
        }
        save(chapterName,false)
    }

    fun save(chapterName: String="",doSave:Boolean=true) {
//        //文章中没有此数据就不要保存防止下一次进来没数据
//        if(chapterName!="defaultChapter" && doc.indexOf(chapterName) == -1) {
//            return
//        }
        if(chapterName.length>0) {
            this.chapterName = chapterName
        }
        wordMode=null
        index=0
        var mWords = chapterMapWords[chapterName]
        if(mWords!=null) {
            bgRun{
                chapterWordModels = Article.getWords(mWords)
                upWords(chapterWordModels)
            }
        }

        if(doSave) {
            userBook(this)
        }
        upStatus.value++
    }

    fun upWords(wds:List<WordModel>){
        words = wds
        next(0)
        event.onChaptersChange.invoke(instance)
    }

    fun next(d:Boolean):Boolean {
        val map = arrayMapOf(true to 1,false to -1)
        return next(map[d]!!)
    }

    fun next(n:Int=1):Boolean {
        if(n==1 && !HomeEvents.onNextWordBefore()){
            return true
        }
        this.index+=n
        if(words.size==0 || index>= words.size || index<0) return false
        val word  =
            if(n==0 && this.wordMode!=null && !(this.wordMode!!.word in words.map{it.word}))
                this.wordMode
            else
                words[index]
        this.size = words.size
        next(word!!)
        if(index<size && index>=0) {
            return true
        }
        return false
    }

    fun next(word: WordModel) {
        this.word = word.word
        wordMode = word
        if (word != null && word.ch != null) {
            this.ch = word.ch!!
        }
        //保存单词进度
        if (index > 0) {
            BookHistroy.lastWord(word.word)
        }
        event.onWordChange.invoke(instance, word)
    }

    fun setWordByString(word:String) {
        index = max(0,words.map{it.word}.indexOf(word))
        next(0)
    }

    fun setContent(s: String, hashData: LinkedHashMap<String, List<String>>) {
        name = s.replace(regex = Regex("\\.\\w+$"),"")
        val rows=hashData.map {
            it.key +"\n"+ it.value.joinToString ( "," )
        }.joinToString ("\n\n" )
        initByData(rows)
    }

}

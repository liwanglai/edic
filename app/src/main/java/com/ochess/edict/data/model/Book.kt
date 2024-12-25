package com.ochess.edict.data.model

import android.os.Handler
import android.view.ViewGroup
import android.widget.ListAdapter
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.BookConf.Companion.mPid
import com.ochess.edict.data.config.BookConf.Companion.words
import com.ochess.edict.data.config.BookConf.Companion.instance as book
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.view.skin.LayoutJdc.Views.Adapter
import java.util.Collections
import java.util.Locale
import java.util.Random

class Book {
    companion object{
        //下载课本
        fun downLoad(file: String, name: String) {
            FileUtil.getData(file) {
                Article.add(it, name, VirtualCommonItem.getId(VirtualCommonItem.Type.donwload))
            }
        }

        fun getHaveBooks(layoutId:Int,onBindViewHolder:(ViewGroup, BookConf)->Unit ): Adapter {
            var ap = Adapter(ActivityRun.context(),layoutId,{ it, vItem, pos->
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

        fun getChapterWords(layoutId:Int,onBindViewHolder:(ViewGroup, WordModel)->Unit ): ListAdapter {
            var ap = Adapter(ActivityRun.context(),layoutId,{ it, vItem, pos->
                onBindViewHolder(it,vItem as WordModel)
            })
            if(words.size>0) {
                ap.setItems(words as ArrayList<Any>)
            }
            return ap
        }


        fun cid(): Int {
            if(book.id ==0) return 0
            val article = Article.find(book.id)
            return article?.cid ?: 0
        }

        fun wordEx(): WordExtend {
            return WordExtend(book.word)
        }

        fun pic(pGet: (u: String?) -> Unit) {
            //var img = "https://cn.bing.com/images/search?q=+"+word
            //img = "https://tse2-mm.cn.bing.net/th/id/OIP-C.31S79ct-_5zHuUbVBUtfLwHaHa"
            var imgFile = PathConf.imgs+book.word+".jpg"
            if(FileUtil.exists(imgFile)) {
                pGet(imgFile)
            }else{
                pGet(null)
            }
        }

        fun getInputKeys(isUpper:Boolean=true,count:Int=0): List<Char> {
            val a = hashSetOf<Char>()
            var w = if(isUpper)book.word.toUpperCase() else book.word.toLowerCase()
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

        private fun getRandChar(usedchars:String,isUpperCase:Boolean=true): String {
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

        fun reload() {
            book.save(book.chapterName,false)
        }

        fun nextChapter(to:Boolean = false): String? {
            val index = BookConf.chapters.indexOf(book.chapterName)
            val rt =if(index+1<BookConf.chapters.size) BookConf.chapters[index+1] else null
            if(to && rt!=null){
                book.save(rt)
            }
            return rt
        }

        fun name(): String {
            val bname = book.name.replace(Regex("\\..+$"),"")
            return bname
        }
        fun words(all:Boolean=false): String {
            if(!all) {
                return name() + "_" + book.chapterName + "\n" + BookConf.words?.map { it.word }
                    ?.joinToString(",")!!
            }else{
                val words = arrayListOf<String>()
                BookConf.chapterMapWords.map { words.addAll(it.value) }
                return name() + "\n" + words?.joinToString(",")!!
            }
        }

        fun filter(type: Int, keys: List<String>) {
            book.index =0
            var rt = BookConf.chapterWordModels.toTypedArray()
            if(type>-1) {
                val historys = HistoryViewModel.selectByType(type,BookConf.words.map{it.word})
                rt = rt.filter { it.word in historys }.toTypedArray()
            }
            if(keys.size > 0){
                rt = HistoryViewModel.filterByKeys(keys,rt.toList()).toTypedArray()
            }
            book.upWords(rt.toList())
        }
    }







}
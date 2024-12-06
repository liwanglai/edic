package com.ochess.edict.presentation.home.jdc.write_word

import android.speech.tts.Voice
import android.widget.EditText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.jdc.book
import java.util.Collections
import java.util.Locale


class ListenWritePlay {
    //播放类型
    enum class BType {
        english,
        chinese
    }
    enum class BMode {
        正序,
        倒序,
        乱序,
        单词长度正序,
        单词长度倒序,
        单词字母正序,
        单词字母倒序,
    }
    companion object {
        val config = UserStatus()
        var bType = BType.english
        var bTimes = 3
        var bSleeps = 3
        var bMode by  mutableStateOf(BMode.正序)
        var bPlay = false
        var bUpper = true
        val tts = GlobalVal.tts.apply {
            setOnCompleted {
                play(true)
            }
        }
        var bIndex = 0
        var bInput =""
        var bEditViews = arrayListOf<EditText>()
        //输入框的显示方式
        var inputStyleLine=false
        //输入方式
        var inputType=false

        var chVoice: Voice? = null
        fun play(auto:Boolean=false){
            //自动播放
            if(auto) {
                //没有开启播放就返回
                if(!bPlay) return
                //休息多少s
                Thread.sleep((bSleeps * 1000).toLong())
                //小于重复次数
                if(bIndex< bTimes-1){
                    bIndex++
                }else{
                    bIndex=0
                    bPlay = book.next()
                }
                //没有下一个就返回
                if(!bPlay) return
            }else{
                tts.stop()
            }
            //读音
            var bWord = if(bType == BType.english) book.word else book.ch
            if(bWord.equals(book.word)) {
//                tts.getSpeech().setVoice(enVoice)
                tts.play(bWord)
            }else {
//                if(chVoice==null) {
//                    var voices = tts.getSpeech().voices
//                    for (voice in voices) {
//                        if (voice.name.startsWith("zh-CN")) {
//                            chVoice = voice
////                            tts.getSpeech().setVoice(chVoice)
////                            tts.play(bWord, chVoice!!.locale)
////                            continue
//                            break
//                        }
//                    }
//                }
//                if(chVoice!=null) {
//                    tts.getSpeech().setVoice(chVoice)
//                }
                tts.play(bWord,Locale.TAIWAN)

            }
        }

        val oldWords = linkedMapOf<String,WordModel>()
        fun upOrder() {
            if(oldWords.size==0){
                BookConf.words.forEach {
                    oldWords.put(it.word,it)
                }
            }
            var newWords = arrayListOf<WordModel>()
            var beforeWords = BookConf.words.subList(0,book.index)
            newWords.addAll(beforeWords)
            var pending =  BookConf.words.subList(book.index,BookConf.words.size-1).map { it.word }
            when(bMode) {
                BMode.正序 -> {
                    oldWords.forEach {
                        if(pending.indexOf(it.key)!=-1) {
                            newWords.add(it.value)
                        }
                    }
                }
                BMode.倒序 -> {
                    val a = oldWords.map{it}
                    Collections.reverse(a)
                    a.forEach {
                        if(pending.indexOf(it.key)!=-1) {
                            newWords.add(it.value)
                        }
                    }
                }
                BMode.单词字母正序 ->{
                    Collections.sort(pending)
                    pending.forEach {
                        newWords.add(oldWords[it]!!)
                    }
                }
                BMode.单词字母倒序 ->{
                    Collections.sort(pending)
                    Collections.reverse(pending)
                    pending.forEach {
                        newWords.add(oldWords[it]!!)
                    }
                }
                BMode.单词长度正序 ->{
                    Collections.sort(pending,object :Comparator<String> {
                        override fun compare(p0: String?, p1: String?): Int {
                            var rt = p0!!.length - p1!!.length
                            if(rt==0) {
                                rt = p0.compareTo(p1)
                            }
                            return rt
                        }
                    })
                    pending.forEach {
                        newWords.add(oldWords[it]!!)
                    }
                }
                BMode.单词长度倒序 ->{
                    Collections.sort(pending,object :Comparator<String> {
                        override fun compare(p0: String?, p1: String?): Int {
                            var rt = p1!!.length - p0!!.length
                            if(rt==0) {
                                rt = p1.compareTo(p0)
                            }
                            return rt
                        }
                    })
                    pending.forEach {
                        newWords.add(oldWords[it]!!)
                    }
                }
                BMode.乱序 ->{
                    Collections.shuffle(pending)
                    pending.forEach {
                        newWords.add(oldWords[it]!!)
                    }
                }
            }
            BookConf.words = newWords
        }
    }
}
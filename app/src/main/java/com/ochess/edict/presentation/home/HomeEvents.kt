package com.ochess.edict.presentation.home

import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.game.inputWord
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.media.Audio

class HomeEvents {
    companion object {
        lateinit var showMainPage: (name:String) -> Unit
        fun onback(){
            when(nowBookShowType){
                "word_shows" -> {
                    nowBookShowType="book_shows"
                    viewMode = MenuConf.mode.chaptarPage
                    false
                }
                "book_shows" -> {
                    nowBookShowType = "word_shows"
                    NavScreen.BookmarkScreen.open()
                    false
                }
                else -> false
            }
        }
    }
    object SwitchMainPage {
        fun onItemClick(item: MenuConf.mode) {
            viewMode = item
            showMainPage(item.name)
            if(!GlobalVal.nav.equals(NavScreen.HomeScreen)) {
                NavScreen.openHome(0)
            }
        }
    }

    object GroupInfoPage {
        fun onWordClick(it: WordModel) {
            GlobalVal.wordViewModel.setWord(DictionarySubEntity(it.wordsetId, it.word,it.level))
            BookConf.instance.setWord(it)
            showMainPage("")
        }

        fun onChapterClick(it: String) {
            nowChapters = it
            GlobalVal.bookmarkViewModel.changeChapter(it)
            GlobalVal.wordViewModel.upList(GlobalVal.wordModelList)
            val word = GlobalVal.wordViewModel.wordState.value.wordModel
            if(word!=null) {
                BookConf.instance.setWord(word)
            }
//            showMainPage()
        }

        /**
         * 排除
         */
        fun onAddDiffSetClick() {

        }

        /**
         * 排除项的单击
         */
        fun onDiffSetItemClick() {
            TODO("Not yet implemented")
        }

        /**
         * 包含项目添加
         */
        fun onAddInSetClick() {
        }

        /**
         * 排除项目单击
         */
        fun onInSetItemClick() {
            TODO("Not yet implemented")
        }

        fun onFunsClick(it: String) {
                when(it){
                    "edit" -> {

                    }
                }
        }

    }

    object scapesGame{
        /**
         * 单词找到事件
         */
        fun onFindWord(wm: WordModel?) {
            if(wm==null) return
            if(wm.isStudyed) return
            ActivityRun.runOnUiThread {
                if(wm.ch!=null) {
                    ActivityRun.msg(wm.ch!!)
                }

                Audio.play(R.raw.correct)
                GlobalVal.tts.speak(wm.word)
            }
        }

        /**
         * 圆盘输入字母键入事件
         */
        fun onInPoint(c: Char, nowWord: String, wordFindOk: Boolean) {
            inputWord.value += c
            Audio.play(R.raw.p7)
        }

        /**
         * 圆盘输入结束事件
         */
        fun onInOver(nowWord: String) {
              inputWord.value = ""
        }
    }

    object exGame{
        //单词的单击事件
        fun onWordClick(wm: WordModel) {
            GlobalVal.tts.speak(wm.word)
        }

    }

}
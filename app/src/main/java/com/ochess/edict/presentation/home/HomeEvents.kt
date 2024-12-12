package com.ochess.edict.presentation.home

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.home.game.inputWord
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.media.Audio

class HomeEvents {
    //状态或者说标志位
    object status  {
        var openDrowUp = true   //开启单指下拉功能
    }
    companion object {
        var downMenuOpen: Boolean by mutableStateOf(false)
        lateinit var showMainPage: (name:String) -> Unit
        val events = Events()


        fun onback(){
            if(HistoryWords.size>1){
                HistoryWords.pop()
                return
            }
            if(!events.onBackPressed()){
                return
            }
            when(nowBookShowType){
                "word_shows" -> {
                    GroupInfoPage.beforMode = viewMode
                    viewMode = MenuConf.mode.chaptarPage
                }
                "book_shows" -> {
                    viewMode = GroupInfoPage.beforMode
                }
                else -> {}
            }
        }
        fun onNextWordBefore():Boolean{
            val modeNow = MenuConf.modeNow()
            //如果保持当前模式开启并且当前模式不等于默认模式则不进入下一个而是直接修改显示模式
            if(!PageConf.getBoolean(PageConf.homePage.RemainViewMode) && viewMode!= MenuConf.mode.chaptarPage && viewMode!=modeNow){
                viewMode = modeNow
                return false
            }
            //下一个的时候如果底部菜单存在 则取消菜单显示
            if(downMenuOpen){
                onDragDownBefore()
            }

            return true
        }
        fun onBackBefore(backfun:()->Boolean) {
            events.onBackPressed = backfun
        }

        fun onDownMenuShow(dfun:()->Unit) {
            events.onDownMenuShow = dfun
        }
        
        fun onDragUp() {
            downMenuOpen = true
            events.onDownMenuShow()
        }

        fun onDownMenuHide(dfun:()->Unit){
            events.onDownMenuHide = dfun
        }
        fun onDragDownBefore(): Boolean {
            if(downMenuOpen){
                downMenuOpen = false
                events.onDownMenuHide()
                return false
            }

            return true
        }

        class Events{
            var onDownMenuHide: () -> Unit ={}
            var onDownMenuShow: () -> Unit ={}
            var onBackPressed: () -> Boolean = {true}
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
        lateinit var beforMode: MenuConf.mode

        fun onWordClick(it: WordModel) {
            BookConf.instance.setWord(it)
            showMainPage("")
            viewMode = beforMode
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
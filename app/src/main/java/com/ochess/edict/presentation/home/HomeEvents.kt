package com.ochess.edict.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.buildAnnotatedString
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.GlobalVal.clipboardManager
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.data.model.Book
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.history.components.HistroyFilter
import com.ochess.edict.presentation.home.game.inputWord
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.InputDialog
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.media.Audio
import com.ochess.edict.view.MPopMenu
import java.text.SimpleDateFormat
import java.util.Date

class HomeEvents {
    //状态或者说标志位
    object status  {
        var openDrowUp = true   //开启单指下拉功能
        var enableDrowUp = true   //开启单指下拉功能

    }
    companion object {
        var downMenuOpen: Boolean by mutableStateOf(false)
        lateinit var showMainPage: (name:String) -> Unit
        val events = Events()


        fun onback(abs:Boolean=false){
            //底部菜单退出和现实方式退出
            if(!abs && !events.onBackPressed()){
                return
            }
            when(nowBookShowType){
                "word_shows" -> {
                    val modeNow = MenuConf.modeNow()
                    //历史记录
                    if(!abs && viewMode == modeNow && HistoryWords.size>1){
                        HistoryWords.pop()
                        return
                    }
                    //其他模式返回主模式 或者返回列表
                    if(viewMode != modeNow && false === PageConf.getBoolean(PageConf.homePage.RemainViewMode,true)){
                        viewMode = modeNow
                    }else {
                        GroupInfoPage.beforMode = viewMode
                        viewMode = MenuConf.mode.chapterPage
                    }
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
            if(!PageConf.getBoolean(PageConf.homePage.RemainViewMode) && viewMode!= MenuConf.mode.chapterPage && viewMode!=modeNow){
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

        fun onDragAbout(x: Float) {
            var pIndex = if(viewMode == MenuConf.mode.chapterPage) 0 else 1
            pIndex += if(x>0) -1 else 1;
            when(pIndex){
                0-> onback(true)
                1-> onback(true)
                2-> NavScreen.BookmarkScreen.open()
                else->{}
            }
        }

        class Events{
            var onDownMenuHide: () -> Unit ={}
            var onDownMenuShow: () -> Unit ={}
            var onBackPressed: () -> Boolean = {true}
        }
    }
    object searchTools{
        var defaultText=""
        fun show(word: String="") {
            GlobalVal.isSearchVisible.value = true
            defaultText = word
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
        var filterType:Int = -1
        var filterLevels:List<String> =listOf<String>()


        fun onWordClick(it: WordModel) {
            //设置单词重新刷新一下位置
            BookConf.instance.setWordByString(it.word)
            GlobalVal.wordViewModel.upList(BookConf.words)
            HistoryWords.reset()
//            showMainPage("")
            viewMode = beforMode
        }

        fun onChapterClick(it: String) {
            nowChapters = it
            GlobalVal.bookmarkViewModel.changeChapter(it)
            if(filterType>-1 || filterLevels.size>0){
                bgRun {
                    Book.filter(filterType, filterLevels)
                }
            }
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

        fun onFunsClick(menu: MPopMenu) {
            menu.show { k, v ->
                when (v.name) {
                    "Save" -> {
                        InputDialog.show(text = Book.words())
                    }
                    "SaveAll" -> {
                        InputDialog.show(text = Book.words(true))
                    }

                    "过滤" -> {
                        HistroyFilter.eventChange{type,date,levels,key->
                            filterType = type
                            filterLevels = levels
                            bgRun {
                                Book.filter(type, levels)
                            }
                        }
                        HistroyFilter.show()
                    }

                    "Copy" -> {
                        val words = BookConf.words.map { it.word }.joinToString(",")
                        clipboardManager.setText(buildAnnotatedString {
                            append(words)
                        })
                        ActivityRun.msg(mt("Copied"))
                    }

                    else -> {
                        val dateNow = Date(System.currentTimeMillis())
                        val date = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(dateNow)
                        val file = PathConf.dcim + BookConf.instance.name + "_" + date + ".jpg"
                        HistoryViewModel.printWordModelsToImg(BookConf.words, file)
                        ActivityRun.msg(mt("export") + file)
                        when (v.name) {
                            "exportAndJumpTo" -> {
                                ActivityRun.openFile(file)
                            }

                            "exportAndOpen" -> {
                                ActivityRun.openImg(file)
                            }
                        }
                    }
                }
            }
        }

        fun onHistoryWordClick(it: WordModel) {
            GlobalVal.wordViewModel.searcher(it.word)
            viewMode = beforMode
        }
    }

    object scapesGame{
        lateinit var lastFindWordMode:WordModel
        /**
         * 单词找到事件
         */
        fun onFindWord(wm: WordModel?) {
            if(wm==null) return
            lastFindWordMode = wm
            if(wm.isStudyed) {
                return
            }
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
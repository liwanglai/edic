package com.ochess.edict.presentation.home.homescreen

import android.os.Handler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.config.MenuConf.mode.*
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.game.ExtGame
import com.ochess.edict.presentation.home.game.LineGameScreen
import com.ochess.edict.presentation.home.game.WordScapesGame
import com.ochess.edict.presentation.home.jdc.recite_word
import com.ochess.edict.presentation.home.jdc.study_word
import com.ochess.edict.presentation.home.jdc.write_word
import com.ochess.edict.presentation.home.nowBookShowType
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.listenbook.ListenBookScreen
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.InputDialog
import com.ochess.edict.presentation.main.extend.setTimeout
import com.ochess.edict.print.MPrinter
import com.ochess.edict.util.ActivityRun

@Composable
fun PageSelect(
    vmode: MenuConf.mode,
    words:ArrayList<String>,
    historyViewModel:HistoryViewModel,
    defContent: @Composable ColumnScope.() -> Unit
) {
    HomeEvents.status.openDrowUp = true
    HomeEvents.status.enableDrowUp = true
    val dSize = Display.getScreenSize()
    Column (modifier = Modifier
        .onGloballyPositioned { layoutCoordinates ->
            HomeEvents.status.openDrowUp =if(vmode in listOf(jdc_select,jdc_write)) true
                else layoutCoordinates.size.height <= dSize.y+20
        }
    ){
        BookConf.onWordChange{word->
            GlobalVal.wordViewModel.setWord(DictionarySubEntity(word.wordsetId,word.word,word.level))
        }
        when (vmode) {
            wordStudy -> {
                defContent()
            }

            jdc_main -> {
                study_word()
//                    JdcFragment()
            }
            jdc_select -> {
                recite_word()
            }
            jdc_write -> {
                write_word()
            }

            wordScapesGame -> {
                WordScapesGame()
            }
            wordExtGame -> {
                ExtGame()
            }
            listenBook -> {
                ListenBookScreen(words)
//                viewMode = wordStudy
            }

            findGame -> {
                HomeEvents.status.enableDrowUp = false
                LineGameScreen(words)
                //ActivityRun.start(GameActivity::class.java.name, words)
//                viewMode = wordStudy
            }
//            print -> {
//                MPrinter.printer.init()
//                    if (MPrinter.printer.canPrint()) {
//                        ActivityRun.msg("打印中...")
//                        historyViewModel.printListByWordModels(GlobalVal.wordModelList)
//                    } else {
//                        ActivityRun.msg("打印机未连接，无法打印！")
//                    }
//            }

//            editBook -> {
//                val book by remember {
//                    mutableStateOf(BookConf.instance.nowBook())
//                }
//                InputDialog.add("Edit Article",book.name,{
//                    val item = InputDialog.eObj as BookItem
//                    item.save(content=it)
//                    ActivityRun.msg("保存成功")
//                    viewMode = wordStudy
//                },{
//                    viewMode = wordStudy
//                })
//                if(book.content!=null) {
//                    InputDialog.show(eObj = book, book.content!!)
//                }
//            }
            //单词列表页
            chapterPage -> {
                val ap = remember {  mutableStateOf(1f) }
                GroupInfoPage(ap)
            }

            else ->{

            }
        }
        //更新展示方式
        MenuConf.modeGroups().forEach{
            for ( v in it.value){
                if(v == vmode){
                    nowBookShowType = it.key
                    break
                }
            }
        }
    }
}
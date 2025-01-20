package com.ochess.edict.presentation.home.homescreen

import android.annotation.SuppressLint
import androidx.collection.arraySetOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.model.Book
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.history.components.HistroyFilter
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.nowChapters
import com.ochess.edict.presentation.home.wGroups
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.FlowRow
import com.ochess.edict.presentation.main.components.InputDialog
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GroupInfoPage(ap: MutableState<Float>){

    val name = BookConf.instance.name
    val funs = listOf("edit")
    val diffSet = remember {
        arraySetOf<String>()
    }
    val inSet = remember {
        arraySetOf<String>()
    }
    Box{
        InputDialog.add("AddArticle","SaveToFile",{
            BookMarkEvent.onAdd(it, 0)
            ActivityRun.msg("Saved")
        })
        val historyViewModel: HistoryViewModel = hiltViewModel()
        HistroyFilter.add(historyViewModel,listOf(
            HistroyFilter.types.type,
            HistroyFilter.types.level
        ))
    }
    LazyColumn (modifier= Modifier
        .padding(30.dp)
        .alpha(ap.value)){

        items(1) {
            /*
               Title("功能")
               Row{
                   funs.forEach {
                       Text(it,Modifier.clickable { HomeEvents.GroupInfoPage.onFunsClick(it) })
                   }
               }



               Title("包含")
               Row{
                   Button(onClick = { HomeEvents.GroupInfoPage.onAddInSetClick() }) {
                       Text("+")
                   }
                   inSet.forEach{
                       Text(it,Modifier.clickable { HomeEvents.GroupInfoPage.onInSetItemClick() })
                   }
               }

               Title("排除")
               Row{
                   Button(onClick = { HomeEvents.GroupInfoPage.onAddDiffSetClick() }) {
                       Text("+")
                   }
                   diffSet.forEach{
                       Text(it,Modifier.clickable { HomeEvents.GroupInfoPage.onDiffSetItemClick() })
                   }
               }
       */
            if(HistoryWords.size>1) {
                Title(mt("WordHistory"))
                FlowRow {
                    HistoryWords.menu.items.reversed().forEach {
                        Text(text = it.title,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    HistoryWords.slice(it.index)
                                    HomeEvents.GroupInfoPage.onHistoryWordClick(it.value as WordModel)
                                }
                        )
                    }
                    HistoryWords.sliceDiscard.reversed().forEach {
                        Text(text = it.word, color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    HomeEvents.GroupInfoPage.onHistoryWordClick(it)
                                }
                        )
                    }
                }
            }

            Title(name.replace(Regex("\\.\\w+$"),"")){
                val pid = Book.cid()
                BookMarkEvent.bookId = BookConf.instance.id
                NavScreen.BookmarkScreen.open("?pid=${pid}")
            }
            if (wGroups.size > 0) {
                FlowRow {
                    wGroups.forEach {
                        val tColor =
                            if (it.equals(nowChapters)) Color.Red else MaterialTheme.colorScheme.primary
                        Text(text = mt(it), color = tColor, fontSize = 14.sp, modifier = Modifier
                            .padding(10.dp)
                            .clickable { HomeEvents.GroupInfoPage.onChapterClick(it) }
                        )
                    }
                }
            }


//            val nowStatus = GlobalVal.wordViewModel.wordState.collectAsState()
//            val nowModel = nowStatus.value.wordModel
//            val words = GlobalVal.wordViewModel.cacheSub()
            if(BookConf.words.size>0) {
                val menu = MPopMenu(listOf(
                    MPopMenu.dataClass("Copy"),
                    MPopMenu.dataClass("export"),
                    MPopMenu.dataClass("Save"),
                    MPopMenu.dataClass("SaveAll"),
                    MPopMenu.dataClass("过滤"),
//                    MPopMenu.dataClass("exportAndOpen"),
//                    MPopMenu.dataClass("exportAndJumpTo")
                )).upMtTitle()
                Column {
                    Title("WordList") {
                       HomeEvents.GroupInfoPage.onFunsClick(menu)
                    }
                    menu.add()
                }
            }
            FlowRow {
                BookConf.words.forEach {
//                val tColor = if(it.isStudyed)  Color.Red else MaterialTheme.colors.primary
                    val tColor = if (it.word.equals(BookConf.instance.word)) Color.Red else MaterialTheme.colorScheme.primary
                    Text(text = it.word, color = tColor,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { HomeEvents.GroupInfoPage.onWordClick(it) }
                    )
                }
            }
        }
    }
}
@Composable
fun Title(txt:String,click:(()->Unit)?=null){
    var more = when(txt){
        "WordList" -> {
            var rt = "(${BookConf.words.size})"
            val cname = BookConf.instance.chapterName
            if(cname.startsWith("Top")){
                rt=LevelViewModel.getTopInfo(cname)
            }
            rt
        }
        else -> {
            if(txt.startsWith("Top")){
                LevelViewModel.getTopInfo(txt)
            }else{
                ""
            }
        }
    }

    Row(modifier = Modifier.padding(start = 0.dp, end = 16.dp, bottom = 6.dp, top = 16.dp)){
        Text(mt(txt)+more, fontSize = 18.sp,color= MaterialTheme.colorScheme.onSurface, modifier = Modifier
            .clickable {
                if(click!=null) click()
            }
        )
    }
}
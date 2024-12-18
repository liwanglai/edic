package com.ochess.edict.presentation.home.homescreen

import android.annotation.SuppressLint
import androidx.collection.arraySetOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.nowChapters
import com.ochess.edict.presentation.home.wGroups
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.FlowRow
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import com.ochess.edict.presentation.main.extend.MText as Text

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
            Title(name.replace(Regex("\\.\\w+$"),"")){
                val pid = BookConf.instance.cid()
                BookMarkEvent.bookId = BookConf.instance.id
                NavScreen.BookmarkScreen.open("?pid=${pid}")
            }
            if (wGroups.size > 0) {
                FlowRow {
                    wGroups.forEach {
                        val tColor =
                            if (it.equals(nowChapters)) Color.Red else MaterialTheme.colorScheme.primary
                        Text(text = it, color = tColor, fontSize = 14.sp, modifier = Modifier
                            .padding(10.dp)
                            .clickable { HomeEvents.GroupInfoPage.onChapterClick(it) }
                        )
                    }
                }
            }


//            val nowStatus = GlobalVal.wordViewModel.wordState.collectAsState()
//            val nowModel = nowStatus.value.wordModel
//            val words = GlobalVal.wordViewModel.cacheSub()
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val clipboardManager = LocalClipboardManager.current
            if(BookConf.words.size>0) {
                val menu = MPopMenu(listOf(
                    MPopMenu.dataClass("Copy"),
                    MPopMenu.dataClass("export"),
//                    MPopMenu.dataClass("exportAndOpen"),
//                    MPopMenu.dataClass("exportAndJumpTo")
                )).upMtTitle()
                Title("WordList") {
                    menu.show { k, v ->
                        when(v.name){
                            "Copy" ->{
                                val words = BookConf.words.map{it.word}.joinToString(",")
                                clipboardManager.setText(buildAnnotatedString {
                                    append(words)
                                })
                                ActivityRun.msg(mt("Copied"))
                            }
                            else -> {
                                val dateNow = Date(System.currentTimeMillis())
                                val date = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(dateNow)
                                val file =PathConf.dcim + BookConf.instance.name  + "_" + date + ".jpg"
                                historyViewModel.printWordModelsToImg(BookConf.words, file)
                                ActivityRun.msg(mt("export") + file)
                                when(v.name) {
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
                menu.add()
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
    Row(modifier = Modifier.padding(start = 0.dp, end = 16.dp, bottom = 6.dp, top = 16.dp)){
        Text(txt, fontSize = 18.sp,color= MaterialTheme.colorScheme.onSurface, modifier = Modifier
            .clickable {
                if(click!=null) click()
            }
        )
    }
}
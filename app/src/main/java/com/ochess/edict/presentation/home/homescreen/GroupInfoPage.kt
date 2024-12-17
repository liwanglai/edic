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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.nowChapters
import com.ochess.edict.presentation.home.wGroups
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.FlowRow
import com.ochess.edict.presentation.navigation.NavScreen
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
            Title("WordList")
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
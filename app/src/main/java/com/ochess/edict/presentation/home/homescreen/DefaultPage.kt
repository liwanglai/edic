package com.ochess.edict.presentation.home.homescreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.glide.rememberGlidePainter
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.plug.CategoryTree
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookmarkViewModel
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.TAG
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.WordState
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.px2dp
import com.ochess.edict.presentation.navigation.NavScreen


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultPage(navController: NavHostController,
                wordIndex: Int?,
                fromPage: Int,
                wordViewModel: WordModelViewModel,
                bookmarkViewModel: BookmarkViewModel,
                historyViewModel: HistoryViewModel,
                onToggleTheme: () -> Unit,
                le: Int? = null
) {
    val wordModelState by wordViewModel.wordState.collectAsState()
    val wordModel = wordModelState
    val detailState by wordViewModel.detailState.collectAsState()
    if(!detailState && PageConf.getBoolean(PageConf.homePage.DefaultShowDetails,true)){
        wordViewModel.searchByText()
    }
    val level by wordViewModel.selectLevel.collectAsState()
    val selectLevel = level > -1 || BookConf.instance.name.isNotEmpty()
    val hasWord = wordModel.wordModel?.word?.isNotEmpty() == true
    HistoryWords.add(wordModel.wordModel)
    Log.d(TAG, "hasWord = $hasWord, selectLevel = $selectLevel, detail = $detailState")


    if (hasWord) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 50.dp, end = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val wordCanEdit = UserStatus().getMutableStatus("wordCanEdit")
                val isEdit =  (wordCanEdit.value==1)
                if(!isEdit) {
                    val word = wordModel.wordModel?.word ?: "";
                    Text(
                        text = word,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp,
                            letterSpacing = 0.15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .combinedClickable(
                                onLongClick = {
                                    //长按如果支持编辑则编辑 否则则搜索
                                    if (PageConf.getBoolean(PageConf.homePage.TitleClickEdit,true)) {
                                        wordCanEdit.value = 1
                                    }else{
//                                        if (wordModel.wordModel?.word.isNullOrEmpty()) {
//                                            navController.navigate(NavScreen.LevelScreen.route) {
//                                                launchSingleTop = true
//                                            }
//                                            wordViewModel.nextDictionaryWord()
//                                        } else {
                                            HomeEvents.searchTools.show(word);
//                                        }
                                    }
                                },
                                onClick = {
                                    HomeEvents.searchTools.show(word);
                                },
                                onDoubleClick = {
                                    if(HistoryWords.size>1) {
                                        HistoryWords.menu.show { _, v ->
                                            wordViewModel.searcher(v.name)
                                            HistoryWords.slice(v.index)
                                        }
                                    }
                                }
                            )
                            //.fillMaxWidth()
                            .align(Alignment.Center)
                    )
                    HistoryWords.menu()
                }
                if(isEdit) {
                    val word = wordModel.wordModel?.word
                    val ch = wordModel.wordModel?.ch
                    wordViewModel.searchByText()
                    if (word != null && ch != null) {
                        TitleEdit(word,ch){ it ->
                            wordCanEdit.value = if(it) 1 else 0
                            if(isEdit){
                                wordModel.wordModel?.let {
                                    it.status = WordModel.STATUS_KNOW
                                    wordViewModel.insertHistory(it)
                                    onOpenNextWord(wordIndex?:0,fromPage)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (!detailState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (selectLevel) {
                        wordViewModel.searchByText()
                    } else {
//                        navController.navigate(NavScreen.LevelScreen.route) {
//                            launchSingleTop = true
//                        }
                        Thread {
                            if (Article.isEmpty()) {
                                CategoryTree.rsyncDataPromPathAssets()
                            }
                        }.start()
                        NavScreen.BookmarkScreen.open()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if(GlobalVal.isSearchVisible.value) {
                //搜索的时候给个空白页面
            } else if (selectLevel) {
                Text(
//                    text = "请回忆单词发音和释义",
                    text = stringResource(R.string.Recall),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
//                    text = "点击屏幕显示答案",
                    text = stringResource(R.string.Click),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
//                    text = "请点击选择词库",
                    text = stringResource(R.string.Selectdatabase),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

//    AnimatedVisibility(
//        visible = detailState && hasWord,
//        enter = fadeIn(),
//        exit = fadeOut()
//    )
    if(detailState && hasWord){
        SearchComponent(wordModel,  wordViewModel){
            onOpenNextWord(wordIndex?:0, fromPage)
            HistoryWords.reset()
        }
    }
}

inline fun onOpenNextWord(wordIndex:Int,fromPage:Int){
    val uOrder = PageConf.getBoolean(PageConf.homePage.NextUnordered)
    GlobalVal.wordViewModel.showNext(uOrder)

//    var wIndex = wordIndex?:0
//    when (fromPage) {
//        PAGE_FROM_BOOKMARK , PAGE_FROM_HISTORY -> {
//            wIndex++
//        }
//    }
//    if(wIndex != wordIndex) {
//        ActivityRun.runOnUiThread {
//            GlobalVal.nav.navigate("${NavScreen.HomeScreen.route}?wordIndex=${wIndex}?fromPage=${fromPage}?level=-1")
//        }
//    }
}

@Composable
fun SearchComponent(
    wordState: WordState,
    wordViewModel: WordModelViewModel,
    nextDo: (t:Int)->Unit
) {

    val vScsroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 32.dp)
            .verticalScroll(vScsroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            shape = RoundedCornerShape(8.dp),
            //backgroundColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                    .fillMaxWidth()
        ) {

            Box {
                if(wordState.wordModel!=null && wordState.wordModel?.hasPic()!!) {
                    val iSize = remember {
                        val ds = Display.getScreenSize()
                        var w= px2dp((ds.x*0.75).toInt())
                        var h= px2dp((ds.y*0.75).toInt())
                        if (ds.x > ds.y) {
                            w = px2dp(ds.x)
                        } else {
                            h=px2dp((ds.y * 0.618).toInt())
                        }
                        DpOffset(w,h)
                    }
                    Image(
                        painter = rememberGlidePainter(request = wordState.wordModel.pic),
                        contentDescription = "pic",
                        modifier = Modifier
                            .alpha(0.4f)
                            .size(iSize.x,iSize.y)
                            .align(Alignment.TopCenter)
                    )
                }

                Column {
                    SearchContent(wordState.wordModel,  wordViewModel)
                }
            }

        }

        AnimatedVisibility(visible = true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FunctionButtons(
                    viewModel = wordViewModel,
                    wordState = wordState,
                    onNextWord = nextDo
                )
            }
        }
    }
}
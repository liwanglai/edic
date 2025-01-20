package com.ochess.edict.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.BookConf.Companion.instance
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookmarkViewModel
import com.ochess.edict.presentation.bookmark.data.BookMark
import com.ochess.edict.presentation.history.BookHistroy
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.history.HistoryViewModel.Companion.UserSettingKey_CanEdinhos
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.home.homescreen.DefaultPage
import com.ochess.edict.presentation.home.homescreen.PageSelect
import com.ochess.edict.presentation.home.homescreen.SlidingEnable
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.Display.setBitMapByPageScreen
import com.ochess.edict.presentation.main.extend.MainRun
import com.ochess.edict.presentation.main.extend.oneRun
import com.ochess.edict.presentation.main.extend.setTimeout
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.DateUtil
import com.ochess.edict.view.MPopMenu
import com.ochess.edict.view.SearchTool

var dictionaryStringBuilder = StringBuilder()
const val TAG = "HomeScreen"

const val PAGE_FROM_HOME = 0
const val PAGE_FROM_LEVEL = 1
const val PAGE_FROM_BOOKMARK = 2
const val PAGE_FROM_HISTORY = 3


var viewMode by  mutableStateOf(MenuConf.modeNow())

var nowChapters by mutableStateOf("")
var wGroups = arrayListOf<String>()
//当前书本
var nowBook = ""
var nowBookShowType = "word_shows" //word_shows book_shows

val homeReSetDefaultBook = mutableStateOf(false)
val wordList = mutableStateOf(arrayListOf<String>())

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
//    scaffoldState: ScaffoldState,
    wordIndex: Int?,
    fromPage: Int,
    wordViewModel: WordModelViewModel,
    bookmarkViewModel: BookmarkViewModel,
    historyViewModel: HistoryViewModel,
    onToggleTheme: () -> Unit,
    le: Int? = null
) {

    //组织全局数据
    var words = remember {
         //返回功能
         ActivityRun.onBackPressed {
            HomeEvents.onback()
            var nav = GlobalVal.nav.currentDestination
            !nav!!.route!!.startsWith(NavScreen.HomeScreen.route)
         }
         BookConf.onBookChange {
             nowBook = name
             initArticle()
             setTimeout(10) {
                 HistoryWords.reset()
             }
         }
         BookConf.onChaptersChange{
             wGroups = BookConf.chapters
             nowChapters = BookConf.instance.chapterName
             wordList.value.clear()
             wordList.value.addAll(BookConf.words.map { it.word })

             GlobalVal.wordModelList = BookConf.words
             GlobalVal.wordViewModel.upList(GlobalVal.wordModelList)

             setTimeout(10) {
                 HistoryWords.reset()
             }
             oneRun{
                 val word = BookHistroy.lastWord()
                 if (word.length > 0) {
                     setWordByString(word)
                 }
             }
         }

        BookConf.initOnce()
        wordList.value
    }

    when (fromPage) {
        PAGE_FROM_LEVEL -> {
            GlobalVal.wordModelList = wordViewModel.cacheSub()
        }

        PAGE_FROM_HOME, PAGE_FROM_BOOKMARK -> {
            if(homeReSetDefaultBook.value){
                BookConf.setBook()
                homeReSetDefaultBook.value=false
            }
            GlobalVal.wordModelList = BookConf.words
        }

        PAGE_FROM_HISTORY -> {
            if(GlobalVal.historyGroup.size>0){
                homeReSetDefaultBook.value = true
            }
        }
    }

    if (wordViewModel.wordState.value.wordModel == null && GlobalVal.wordModelList.size > 0) {
        wordViewModel.upList(GlobalVal.wordModelList)
    }
    val next:@Composable BoxScope.()->Unit = {
        PageSelect(viewMode,words, historyViewModel) {
            DefaultPage(
                navController,
                wordIndex,
                fromPage,
                wordViewModel,
                bookmarkViewModel,
                historyViewModel,
                onToggleTheme,
                le
            )
        }
        if(HomeEvents.downMenuOpen && nowBookShowType.equals("word_shows") && !GlobalVal.isSearchVisible.value){
            Box (modifier = Modifier
                .clickable  { HomeEvents.onback(true) }
            ){
                Icon(
                    painter = painterResource(id = R.drawable.abc_ic_ab_back_material),
                    contentDescription = "返回单词列表",
                    tint = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.size(25.dp)
                )
                Text(mt("backToWordList"), modifier = Modifier.padding(start=20.dp))
            }
        }
        Row(modifier = Modifier.padding(10.dp,15.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            if (GlobalVal.isSearchVisible.value) {
                SearchTool(wordViewModel,HomeEvents.searchTools.defaultText)
                if (wordViewModel.wordState.value.wordModel != null) {
                        wordViewModel.detailState.value = true
                }
            } else {
                val wordsMode = remember {
                    MenuConf.modeGroups()["word_shows"]
                }
                if(viewMode in wordsMode!!) {
                    IconButton(
                        {
                            HomeEvents.searchTools.show();
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .size(20.dp, 20.dp)
                        )
                    }
                }
            }
            if(false) {
                val todayEdinhosSize = historyViewModel.edinhosSize.collectAsState()
                val isnDo = historyViewModel.canEdinhos.collectAsState()
                if (todayEdinhosSize.value > 0) {
                    IconButton(
                        onClick = {
                            NavScreen.openRoute(NavScreen.routes.HistorySearchByEdinhouse)
                            //配置学习了的参数
                            UserStatus.set(DateUtil.dayRemainderMs()) {
                                it.putBoolean(UserSettingKey_CanEdinhos, false)
                                historyViewModel.canEdinhos.value = false
                                return@set UserSettingKey_CanEdinhos
                            }
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edinhos),
                            contentDescription = "爱丁浩斯学习",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(25.dp)
                        )
                        if (isnDo.value) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(Color(0xd0ff0000))
                                    .padding(1.dp)
                                    .size(14.dp)
                            ) {
                                Text(
                                    text = String.format("%2d", todayEdinhosSize.value),
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    fontStyle = FontStyle.Normal,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                }

                if (wGroups.size > 0) {
                    var menu = MPopMenu.Unspecified
                    Box(
                        modifier = Modifier.padding(10.dp).combinedClickable(
//                        onClick = {
//                           // NavScreen.openRoute(route = NavScreen.routes.Switch)
//                        },
                            onClick = {
                                val words = GlobalVal.wordViewModel.cacheSub()
                                menu.upItems(words.map {
                                    MPopMenu.dataClass(
                                        it.word,
                                        value = it,
                                        selected = it.word.equals(BookConf.instance.word)
                                    )
                                } as ArrayList).show { k, v ->
                                    HomeEvents.GroupInfoPage.onWordClick(v.value as WordModel)
                                }
                            },
                            onLongClick = {
                                menu.upItems(wGroups.map {
                                    MPopMenu.dataClass(it, selected = it.equals(nowChapters))
                                } as ArrayList)
                                menu.show { k, v ->
                                    nowChapters = k
                                    bookmarkViewModel.changeChapter(k)
                                    wordViewModel.upList(GlobalVal.wordModelList)
                                }

                            }
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.view_mode),
                            contentDescription = "章节",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    menu.add()
                }
            }
//            IconThemeSwitch(modifier = Modifier.align(Alignment.End)) {
//                onToggleTheme()
//            }
        }
        MainRun(1000) {
            setBitMapByPageScreen("0")
        }
    }

//    PageConf.getBoolean(PageConf.homePage.UpDownDraggable,true)
    SlidingEnable( false ) {
        next()
    }
}
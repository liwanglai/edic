package com.ochess.edict.presentation.home.game

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.model.WordExtend
import com.ochess.edict.presentation.home.HomeEvents.scapesGame.lastFindWordMode
import com.ochess.edict.presentation.home.game.wordScapes.WordScapes
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.util.ActivityRun


//已经找到的单词
var findedWords = mutableStateListOf<String>()
//当前书本
val book = BookConf.instance
//书本单词切换之后的当前单词
var bookNowWord = mutableStateOf(book.word)
//圆盘输入的字母列表
var inputWord = mutableStateOf("")
//导航按钮是否显示
var navButtonVisitable by  mutableStateOf(false)
var toNextWord:()->Unit = {}

/**
 * 划词游戏
 */
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WordScapesGame() {
    var eWord = WordExtend(book.word)
    val flen = PageConf.getInt(PageConf.sGamePage.LetterLen)

    fun isOk(): Boolean {
        if(eWord.isize + eWord.data.size > 1){
            if(flen!=0) {
                if(eWord.name.length!=flen) return false
            }
            return true
        }
        return false
    }
    fun nextWord(): Boolean {
        while (book.next()) {
            eWord = WordExtend(book.word)
            if (isOk()) return true
        }
        return false
    }

    val ws = remember {
        WordScapes()
    }

    var name = eWord.name
    var allWords = arrayListOf<String>()
    //下拉单词单击选择 刷新视图
    val wordModel = GlobalVal.wordViewModel.wordState.collectAsState()
    val word = wordModel.value.wordModel
//    LaunchedEffect(key1 = word) {
//    }
    fun initWord(){
        bookNowWord.value = book.word
        name = eWord.name
        allWords = arrayListOf<String>()
        findedWords.clear()
        allWords.addAll(eWord.data)
        if(PageConf.getBoolean(PageConf.sGamePage.InLetter,true)) {
            allWords.addAll(eWord.inword)
        }

        val words = allWords.filter { it.length<=book.word.length }
        ws.setWord(name, words, eWord.word)
    }
    toNextWord = {
        if (nextWord()) {
            initWord()
        }
    }
    ws.onFinish {
        if(PageConf.getBoolean(PageConf.sGamePage.AutoNext)){
            toNextWord()
            return@onFinish
        }
        navButtonVisitable = true
    }
    Log.d("WordScapesGame: ",allWords.joinToString(","))
    if(isOk()) initWord() else toNextWord()
    sGameView(ws)
}
@Composable
fun sGameView(ws: WordScapes)
{
//    LaunchedEffect(key1 = bookNowWord.value) {
//
//    }
    val pSize = Display.getScreenSize()
    //是否宽度为主的屏幕
    val isWScreen = ActivityRun.isHorizontalScreens()
    val px = Display.px2dp(pSize.x).value
    Box {

      Column (Modifier.fillMaxSize()) {
        myTitle(ws, bookNowWord.value)

        if (isWScreen) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val size = pSize.y / 15f
                    ws.outgrid(size)
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectDragGestures(onDragStart = {
                            }, onDragEnd = {
                            }, onDrag = { change, dragAmount ->
                            })
                        }

                ) {
                    val px = Display.px2dp(pSize.y).value
                    Spacer(modifier = Modifier.weight(1F))
                    ws.incircle(size = (px / 2).toInt())
                    Spacer(modifier = Modifier.weight(1F))
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Display.px2dp(pSize.y) * 0.618f)
            ) {
//            val size = 80f
                val size = pSize.x / 15f
                ws.outgrid(size)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Log.d("sGameView: ", "x:" + pSize.x)
                ws.incircle(size = (px / 2).toInt())
            }
        }
      }
      if(navButtonVisitable) {
          Column {
              Spacer(modifier = Modifier.weight(5f))
              Row(
                  horizontalArrangement = Arrangement.Center,
                  modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp)

              ) {
                  Text(text = "重新开始", modifier = Modifier.clickable {
                      ws.refrash()
                      navButtonVisitable = false
                  })
                  Spacer(modifier = Modifier.weight(1f))
                  Text(text = "下一个", modifier = Modifier.clickable {
                      toNextWord()
                      navButtonVisitable = false
                  })
              }
          }
      }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun myTitle(ws: WordScapes, word: String) {
    val title = remember { mutableStateOf(word) }
    //标题单击提示的下一个单词
    val nw = ws.nextWord.collectAsState()
    //before word
    val bw = remember { mutableStateOf(nw.value) }
    //输入单词有改动的时候
    LaunchedEffect(key1 = inputWord.value) {
        if (ws.nowWord.length > 0) {
            title.value = ws.nowWord
        }else{
            if(findedWords.size>0) {
                title.value = findedWords.last()
            }
        }
    }
    //下一个单词变动的时候
    LaunchedEffect(key1 = nw.value) {
        if(bw.value.word.length>0) {
            title.value = bw.value.word
        }
        bw.value = nw.value.copy()
    }
    Text(title.value, modifier = Modifier
        .padding(10.dp)
        .combinedClickable(onClick = {
            if (nw.value.ch != null && !title.value.equals(nw.value.ch)) {
                title.value = nw.value.ch!!
                return@combinedClickable
            }
            val out = if(navButtonVisitable) nw.value.word else lastFindWordMode.ch
            ActivityRun.msg(out!!)
        }, onDoubleClick = {
            ws.resetNextWord()
        })
    )
}

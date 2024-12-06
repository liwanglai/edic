package com.ochess.edict.presentation.home.game.ext

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.px2dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object WordExtGame {
    var textColor = Color.Black

    @Composable
    private fun base(points:List<WordModel>,wordModel:WordModel,startAngle:Double,type:Int=2){
        val word:String = wordModel.word
        val sc = Display.getScreenSize()
        val w = px2dp(min(sc.x,sc.y))
        var r = w/2
        val showChMap = remember {
            mutableStateMapOf<String,Boolean>()
        }
        textColor = MaterialTheme.colors.primary

        Box(modifier = Modifier.border(1.dp,Color.LightGray)) {
            @Composable
            fun ct(
                text: String,
                pos: DpOffset,
                tColor: Color = Color.Black,
                alignLeft: Boolean = true,
                onClick: () -> Unit = {}
            ) {
                var tw = 0f
                val lineH = 10.dp
                if (!alignLeft) {
                    tw = text.length * 8.dp.value
                }

                Text(text, color = tColor, fontSize = 14.sp, modifier = Modifier
                    .offset(pos.x - tw.dp, pos.y - lineH)
                    .clickable {
                        onClick()
                    })
            }
            class line{
                var color: Color = Color.Gray
                var start: DpOffset = DpOffset.Zero
                var end: DpOffset = DpOffset.Zero
                var ending: String = ""
                var pre: String = ""
                var size: Float = 0f
            }

            val center = DpOffset(r, r)
            val angle = 2 * PI / 3 / points.size //60度
            val lineList = arrayListOf<line>()
            val chText = remember {
                mutableStateOf("")
            }
            //圆心
            val tBoxWith=(word.length*8.dp.value).dp
            val offsetChBox = when(type){
                0 ->  DpOffset(tBoxWith,r-r/3-4.dp)
                1 ->  DpOffset(r/2,r/4)
                2 ->  DpOffset(r+tBoxWith,r-r/3-4.dp)
                else -> DpOffset(0.dp,0.dp)
            }
            ct(text = word, center, tColor = textColor){
                chText.value = wordModel.ch.toString()
                showChMap.forEach { t, u ->
                    showChMap.set(t, false)
                }

                HomeEvents.exGame.onWordClick(wordModel)
            }
            Column (verticalArrangement = Arrangement.Center, modifier = Modifier
                .offset(offsetChBox.x, offsetChBox.y)
                .padding(20.dp)
                .size((r.value * 0.7).dp, r / 2)
//                .border(1.dp, Color.LightGray)
            ){
                Text(text=chText.value, color = Color.Red, fontSize = 15.sp, modifier = Modifier
//                .background(Color.Blue)
                    .align(if(type==1) Alignment.CenterHorizontally else Alignment.Start)
                )
            }


            points.forEachIndexed { index,wm ->
                val ad = startAngle + angle * index
                val start = DpOffset(
                    r + (r.value / 3 * 2 * cos(ad)).dp,
                    r + (r.value / 3 * 2 * sin(ad)).dp
                )

                var lineColor = Color.Gray
                var lineSize = 2f
                var pre = ""
                var ending = ""
                //单词的前缀和后缀数据赋值
                if (wm.word.matches(Regex(".+$word.+"))) {
                    val preEnding = wm.word.split(word)
                    pre = preEnding.first()
                    ending = preEnding.last()
                } else if (wm.word.startsWith(word)) {
                    ending = wm.word.replace(word, "")
                } else if (wm.word.endsWith(word)) {
                    pre = wm.word.replace(word, "")
                }
                //显示中文的时候
                if (showChMap.get(wm.word) == true) {
//                    ct(wm.ch.toString(), pos = DpOffset(start.x, (start.y.value - 50).dp))
                    lineColor = Color.Red
                    lineSize = 10f
//                    textColor = Color.Red
                }
                val le = line()
                le.pre=pre
                le.ending=ending
                le.start = start
                le.color=lineColor
                le.size = lineSize
                if (pre.length > 0) {
                    ct("$pre", pos = start, tColor = textColor, false) {
                        showChMap.forEach { t, u ->
                            showChMap.set(t, false)
                        }
                        showChMap.set(wm.word, true)
                        chText.value = wm.ch.toString()
                        HomeEvents.exGame.onWordClick(wm)
                    }
                }

                if (ending.length > 0) {
                    val end = DpOffset(
                        r +tBoxWith+ (r.value/3 * cos(ad + PI)).dp,
                        r + (r.value/3 * sin(ad + PI)).dp
                    )
                    le.end  = DpOffset(end.x, (end.y))
//                    if (showCh.value) {
//                        ct(wm.ch.toString(), pos = le.end)
//                    }
                    ct("$ending", pos = end, tColor = textColor){
                        showChMap.forEach { t, u ->
                            showChMap.set(t, false)
                        }
                        showChMap.set(wm.word, true)
                        chText.value = wm.ch.toString()
                        HomeEvents.exGame.onWordClick(wm)
                    }
                }
                lineList.add(le)
            }

            Canvas(
                modifier = Modifier
                    .size(w)
            ) {
                //左侧120度
                lineList.forEach {
                    it.apply {
                        if (pre.length > 0) {
                            drawLine(
                                start = Offset(start.x.toPx(), start.y.toPx()),
                                end = Offset(center.x.toPx(), center.y.toPx()),
                                color = color,
                                strokeWidth = size
                            )
                        }
                        if (ending.length > 0) {
                            drawLine(
                                start = Offset(end.x.toPx(), end.y.toPx()),
                                end = Offset((center.x+tBoxWith).toPx(), center.y.toPx()),
                                color = color,
                                strokeWidth = size
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun out(word: String, points: List<WordModel>) {
        val left= arrayListOf<WordModel>()
        val right= arrayListOf<WordModel>()
        val middle= arrayListOf<WordModel>()
        val size = 12
        var wordModel:WordModel? = null
        points.forEach{
            if(it.word.matches(Regex(".+$word.+"))){
                middle.add(it)
            }else if(it.word.equals(word)) {
                wordModel = it
            }else if(it.word.startsWith(word)) {
                right.add(it)
            }else if(it.word.endsWith(word)){
                left.add(it)
            }
        }

        if(wordModel!=null) {
            left.chunked(size).forEach {
                base(it, wordModel!!, PI *2/3)
            }
            right.chunked(size).forEach {
                base(it, wordModel!!, PI *2/3, 0)
            }
            middle.chunked(size).forEach {
                base(it, wordModel!!, PI *2/3, 1)
            }
        }
    }

}
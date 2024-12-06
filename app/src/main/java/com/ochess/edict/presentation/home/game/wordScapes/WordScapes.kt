package com.ochess.edict.presentation.home.game.wordScapes

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.R
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.game.findedWords
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.dp2px
import java.util.Collections

class WordScapes() : WordScapesData(){
    /**
     * 显示的矩阵 只是背景图形没有文字
     */
    @Composable
    fun outgrid(size:Float=80f) {
        Box(modifier = Modifier.fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                wordMap.forEach { s, wordPos ->inCircleChars
                    var x = wordPos.x
                    var y = wordPos.y
                    val z = wordPos.z
                    s.forEach {
                        drawRoundRect(
                            Color.LightGray,
                            Offset(x * (size+size*0.1f), y * (size+size*0.1f)),
                            Size(size,size)
                        )
                        if (z == 0) x++
                        else y++
                    }
                }
            }
            findedText((size+size*0.1).toInt())
        }

    }

    /**
     * 矩阵中空格中的字母显示
     */
    @Composable
    protected fun findedText(of:Int =90) {
        Log.d("findedText: ", "fsize:${findedWords.size}")
        Box (modifier = Modifier.fillMaxSize()){
            val fLast = if(findedWords.size>0)  findedWords.last() else ""
            findedWords.forEach {
                val wp = wordMap[it]
                if (wp != null) {
                    var x = wp.x
                    var y = wp.y
                    var fColor = Color.Black
                    wp.finded = true
                    if(fLast.equals(it)) fColor = Color.Red

                    it.forEach {
                        var fWeight = FontWeight.Normal
                        if(multipleUsedPoint.indexOf(IntOffset(x,y))>-1) fWeight = FontWeight.Bold
                        Text(
                            text = it.toString(), fontWeight = fWeight, color = fColor, modifier = Modifier
//                                .border(1.dp,Color.Gray)
                                .size(25.dp)
                                .offset(Display.px2dp((x * of + of*0.35).toInt()), Display.px2dp((y * of + of*0.2).toInt()))
                        )
                        if (wp.z == 0) x++
                        else y++
                    }
                }
            }
        }
    }

    /**
     * 输入圆盘
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    @Preview
    fun incircle(
                 size: Int =360,
                 alpha: Float =0.5f,
                 bColor: Color = Color.Gray,
                 fSize: TextUnit = 30.sp,
                 pointR:Int = 25,
                 lineSize:Int = 5
    ) {

        val a = inCircleChars.value.map{ it }
        val rews:()->Unit = {
            cPoints.clear()
            Collections.shuffle(a)
            inCircleChars.value=a
        }
        val angle = 2*Math.PI / a.size
        val r = size/2
        LaunchedEffect(key1 = Unit) {
            rews()
        }
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(bColor.copy(alpha = alpha), shape = RoundedCornerShape(size.dp))
        ) {
            inCircleChars.value.forEachIndexed { index, c ->
                val ad = angle * index
                val innerR = r-pointR;
                val tSize = (fSize/2).value
                val x = r + innerR * Math.cos(ad)
                val y = r + innerR * Math.sin(ad)
                cPoints.add(charPos(Display.dp2px(x),Display.dp2px(y), c))
                Text(
                    text = c.toString(),
                    color = Color.Black,
                    fontSize = fSize,
                    modifier = Modifier
                        .offset((x-tSize).dp, (y-tSize).dp)
                        .alpha(1f)
                )
            }
            drawLine(bColor, lineSize, pointR)
            Icon(painter = painterResource(id = R.drawable.reset_ico), contentDescription =null, modifier = Modifier
                .size(50.dp)
                .offset((r-25).dp,(r-25).dp)
                .alpha(0.35f)
                .combinedClickable (
                    onClick = rews,
                    onLongClick = {
                        refrash()
                    }
                )
            )

        }
    }

    /**
     * 输入圆盘画线
     */
    @SuppressLint("UnrememberedMutableState")
    @Composable
    protected fun drawLine(
        bColor: Color,
        lineWith:Int=5,
        radius:Int=25
    ) {
        val cr = dp2px(radius.dp)
        val lineSize = dp2px(lineWith.dp)
        val points = remember {
            mutableStateListOf<Offset>()
        }

        /**
         * 输入圆盘尝试更改当前选择的点
         */
        fun tryChangePoints() {
            val rs = cr
            val dPoint = drPoint.value
            var inPoint = false

            var i = -1
            for(p in cPoints){ i++
                val rlen = Math.sqrt(
                    Math.pow(Math.abs(dPoint.x - cPoints[i].x).toDouble(), 2.0)
                            + Math.pow(Math.abs(dPoint.y - cPoints[i].y).toDouble(),2.0)
                )
                if (rlen < rs) {
                    val point = cPoints[i].toPoint()
                    if(points.indexOf(point)==-1 || !lastInPoint) {
                        val c = p.c.toLowerCase()
                        points.add(point)
                        nowWord += c
                        Log.d("drawLine: ","word:$nowWord ${points.size} index:$i")
                        var wordFindOk = false
                        if (inword.indexOf(nowWord) != -1) {
                            onFindWordAdd(nowWord)
                            wordFindOk = true
                        }

                        HomeEvents.scapesGame.onInPoint(c,nowWord,wordFindOk)
                    }
                    inPoint = true
                    break
                }
            }
            lastInPoint = inPoint
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    drPoint.value = charPos(it.x.toDouble(), it.y.toDouble())
                    tryChangePoints()
                }, onDragEnd = {
                    HomeEvents.scapesGame.onInOver(nowWord)
                    nowWord = ""
                    points.clear()
                }, onDrag = { change, dragAmount ->
//                    drPoint.value.apply {
//                        x += dragAmount.x
//                        y += dragAmount.y
//                    }
                    val it = drPoint.value
                    drPoint.value = charPos(it.x + dragAmount.x, it.y.toDouble() + dragAmount.y)
                    tryChangePoints()
                })
            }
        ) {
//            cPoints.mapIndexed { i, p ->
//                drawCircle(bColor, radius = 65f, p)
//            }
            var start: Offset? = null
            var end: Offset? = null

            for (point in points) {
                if (start == null) {
                    start = point
                    end = point
                    continue
                }
                start = end
                end = point
                drawLine(
                    start = start!!,
                    end = end,
                    color = bColor,
                    strokeWidth = lineSize
                )
                drawCircle(bColor, radius = cr, start)
            }
            if (end == null) return@Canvas
            drawCircle(bColor, radius = cr, end)
            //Log.d("drawLine: ","point:${end} , ${drPoint.value}/${points.size}")
            drawLine(
                start = end,
                end = drPoint.value.toPoint(),
                color = bColor,
                strokeWidth = lineSize
            )
        }


    }


}


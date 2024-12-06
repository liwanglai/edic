package com.ochess.edict.presentation.home.components

import android.graphics.Rect
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.print.PrintUtils

lateinit var  fView: View

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun FloatInfoText() {
    val floatText by remember { GlobalVal.floatText}
    val offset by remember { GlobalVal.floatOffet }
    var bSize by remember { GlobalVal.floatSize }
    fView = LocalView.current
    var rc  = Rect()
    (fView.parent as View).getGlobalVisibleRect(rc)

    AnimatedVisibility(visible = floatText.length>0) {
            Box(
                modifier = Modifier.combinedClickable(
                        onClick = {
                            GlobalVal.floatText.value=""
                        },
                        onLongClick ={
                            GlobalVal.wordViewModel.searcher(GlobalVal.floatText.value,true)
                        }
                    )
                    .onSizeChanged { size ->
                        bSize = size
                    }
                    .absoluteOffset {
                        IntOffset(offset.x,offset.y-rc.top)
                    }
                    .zIndex(1000f)
                    .alpha(0.85f)
                    .wrapContentSize(Alignment.Center) // 使内容根据其大小居中
                    .background(Color.Transparent)


            ) {
                Image(
                    painter = painterResource(id = R.drawable.popup) ,
                    null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(
                            DpSize(px2dp(bSize.width),px2dp(bSize.height))
                        )
//            .fillMaxSize()
                )
                Text(
                    text = floatText,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(10.dp)
                )
            }
    }
}

private fun px2dp(width: Int): Dp {
    return PrintUtils.px2dip(fView.context,width.toFloat()).dp
}

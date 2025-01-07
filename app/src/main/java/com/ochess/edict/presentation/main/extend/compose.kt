package com.ochess.edict.presentation.main.extend

import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.Display.px2dp


@Composable
fun MText(text: String,
          modifier: Modifier = Modifier,
          color: Color = Color.Unspecified,
          fontSize: TextUnit = TextUnit.Unspecified,
          textAlign: TextAlign? = null,
          style: TextStyle = LocalTextStyle.current
          ){
    val textNew = if(text.endsWith(":")) mt(text.trim(':')) else mt(text)
    Text(textNew,modifier,color,fontSize=fontSize,style=style,textAlign=textAlign)
}

@Composable
fun HtmlView(source:String,height:Float=1f){
    val p = Display.getScreenSize()
    AndroidView(
        modifier = Modifier
            .width(px2dp(p.x))
            .height(px2dp(p.y)*height),
        factory = { context ->
            WebView(context).apply {
                setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
                setHorizontalScrollBarEnabled(false)
                getSettings().apply {
//                    setUseWideViewPort(true)
//                    setLoadWithOverviewMode(true)
                }
                loadData(source, "text/html", "UTF-8")
            }
        }
    )
}
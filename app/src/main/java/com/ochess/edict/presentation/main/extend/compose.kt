package com.ochess.edict.presentation.main.extend

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.toHtml
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
fun HtmlView(source:String){
    val p = Display.getScreenSize()
    AndroidView(
        modifier = Modifier
            .width(px2dp(p.x))
            .height(px2dp(p.y/2)),
        factory = { context ->
            WebView(context).apply {
                loadData(source, "text/html", "UTF-8")
            }
        }
    )
}
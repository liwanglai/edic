package com.ochess.edict.presentation.main.extend

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
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
import com.ochess.edict.util.ActivityRun
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.annotations.Blocking


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
fun HtmlView(source:String,height:Float=1f,linkBlockOpen:Boolean=true){
    val p = Display.getScreenSize()
    AndroidView(
        modifier = Modifier
            .width(px2dp(p.x))
            .height(px2dp(p.y) * height),
        factory = { context ->
            WebView(context).apply {
                setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
                setHorizontalScrollBarEnabled(false)
                getSettings().apply {
                    if(!linkBlockOpen) {
                        webViewClient = object : WebViewClient() {
                            //重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                url: String
                            ): Boolean {
                                if (url.matches(Regex(".+\\.htm*"))) {
                                    view.loadUrl(url)
                                    //如果不需要其他对点击链接事件的处理返回true，否则返回false
                                    return true
                                } else {
                                    val request = Request.Builder().url(url).build()
                                    bgRun {
                                        OkHttpClient().newCall(request).execute().use { response ->
                                            var textData = response.body?.string() ?: ""
                                            MainScope().launch {
                                                view.loadData(textData, "text/html", "UTF-8")
                                                setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
                                                setHorizontalScrollBarEnabled(false)
                                            }
                                        }
                                    }
                                    return true
                                }
                            }
                        }
                        javaScriptEnabled = true
                    }
//                    setUseWideViewPort(true)
//                    setLoadWithOverviewMode(true)
                }
                loadData(source, "text/html", "UTF-8")
            }
        }
    )
}
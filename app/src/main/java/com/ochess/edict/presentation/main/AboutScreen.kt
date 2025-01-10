package com.ochess.edict.presentation.main

//import androidx.compose.foundation.layout.FlowRow
import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fasterxml.jackson.databind.ObjectMapper
import com.ochess.edict.R
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.config.SettingConf
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.FlowRow
import com.ochess.edict.presentation.main.extend.HtmlView
import com.ochess.edict.presentation.main.extend.MainRun
import com.ochess.edict.presentation.main.extend.MarkdownContent
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.FileUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

@Composable
fun HtmlPage(title:String,htmlCode:String){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                mt(title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Column (Modifier.padding(10.dp)
            //.verticalScroll(rememberScrollState())
        ){
                HtmlView(htmlCode)
        }
    }
}
@Composable
fun AboutScreen() {
    var data = FileUtil.getRes(R.raw.about)
    if(data!=null) {
        data = data.replace("\$email", NetConf.email)
        HtmlPage("about",data)
    }
}
@Composable
fun PrivacyScreen() {
    var data = FileUtil.getRes(R.raw.pp)!!
    HtmlPage("Privacy", data)
}
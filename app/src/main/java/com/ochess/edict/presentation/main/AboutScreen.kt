package com.ochess.edict.presentation.main

//import androidx.compose.foundation.layout.FlowRow
import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ochess.edict.presentation.main.extend.MarkdownContent
import com.ochess.edict.util.FileUtil

@Composable
fun AboutScreen() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                mt("about"),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        Column (Modifier.padding(10.dp)){
            val data = FileUtil.getRes(R.raw.about)
            if(data!=null) {
                HtmlView(data)
            }

            Text(mt("LinkMe"), fontSize = 25.sp)
            Card {
                Text(mt("linkMeInfo") + "：" + NetConf.email)
            }
        }
    }
}
package com.ochess.edict.presentation.main

//import androidx.compose.foundation.layout.FlowRow
import android.annotation.SuppressLint
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
import com.fasterxml.jackson.databind.ObjectMapper
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.config.SettingConf
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.FlowRow


var currentThemeNow:Int = 0
val config = UserStatus()
@Composable
fun SettingScreen() {
    currentThemeNow = if(isSystemInDarkTheme()) 2 else 1
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    mt("Setting"),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
            Column {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val sc = SettingConf()
                    items(sc.toList()) {
                        SettingItem(it.key, it.value, sc)
                    }
                }
            }
        }
    }
}
@SuppressLint("RestrictedApi")
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun SettingItem(name: String, value:String="", setting: SettingConf? =null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .wrapContentHeight()
            .combinedClickable(
                onClick = { if(setting!=null) setting[name]=""  },
            ),
        shape = RoundedCornerShape(8.dp),
//        elevation = 0.dp,
//        backgroundColor = MaterialTheme.colors.surface,
//        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.surface)
    ) {
        //页面配置
        if(value.matches(Regex("page:.+"))){
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                var is_show by remember {  mutableStateOf(false) }
                Row(Modifier.clickable { is_show = !is_show }) {
                    Text(mt(name))
                    Spacer(modifier = Modifier.weight(1f))
                    Text("...")
                }
                val childs = value.substring(5).split(",")
                if(is_show) {
                    FlowRow {
                        childs.forEach {
                            if (PageConf.options.get(it) != null) {
                                config.switchButton(it, PageConf.options.get(it)!!,PageConf.default.get(it))
                            } else {
                                config.booleanButton(it,PageConf.default.get(it))
                            }
                        }
                    }
                }
            }
            return@Card
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(mt(name))
            Spacer(modifier = Modifier.weight(1f))
            Text(mt(value), modifier = Modifier)
        }
        if(SettingConf.options.get(name)!=null){
            SettingConf.options[name]?.upMtTitle()?.add()
        }
    }
}

package com.ochess.edict.presentation.listenbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ochess.edict.util.ActivityRun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.reflect.Type
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.accompanist.glide.rememberGlidePainter
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Book
import com.ochess.edict.exception.TipException
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.presentation.main.components.AddButton
import com.ochess.edict.presentation.main.components.CommonItem
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.FileUtil

var showDialog by mutableStateOf(false)
val TAG: String = "ListenScreen"

/**
 * 文章列表
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ListenScreen(arguments: Bundle?) {
    var docs by remember { mutableStateOf(arrayListOf<LoadJsonItem>()) }
    LaunchedEffect(docs) {
        val aids = arguments!!.getString("aids","")
        if(aids.length>0) {
            val ida = aids.split(",").map{it.toInt()}
            Article.finds(ida){
                val ats = arrayListOf<LoadJsonItem>()
                it.forEach {
                    ats.add(LoadJsonItem("",it.id.toString(),it.name,0))
                }
                docs = ats
            }
        }else {
            val haveBooks = arrayListOf<String>()
            Book.getHaveBooks{
                haveBooks.addAll(it.map { it.name })
            }
            loadList(NetConf.data+"/books.json") {
                docs = it
                    .filter {
                        haveBooks.indexOf(it.preface) == -1
                    } as ArrayList<LoadJsonItem>
            }
        }
    }

    Column {
        Row (
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mt("ListenBooks"),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            AddButton({
                FileUtil.pickFile(onReadFile = { textToRead, file, uri ->
                    Article.add(textToRead, file)
                    NavScreen.ListenBooks.open(uri.toString())
//                    ActivityRun.start(
//                        ListenBookActivity::class.java.name,
//                        arrayListOf(uri.toString())
//                    )
                })
            })

        }
        AnimatedVisibility(visible = docs.size > 0) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                itemsIndexed(docs) { index, item ->
                        ListenBooksItem(item = item)
                }
            }
        }
    }
//        Row(modifier= Modifier
//        .fillMaxSize()
//        .padding(10.dp)
//    ) {
//        docs.forEach({
//            Text(text=it.key,Modifier.clickable {
//                ActivityRun.start(ListenBookActivity::class.java.name, arrayListOf(it.value))
//            })
//        })
//    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListenBooksItem(item:LoadJsonItem){
    CommonItem {
        Row(modifier = Modifier
            .combinedClickable(
                onClick = {
                    val value = item.url
                    NavScreen.ListenBook.open("?words="+value)
//                    ActivityRun.start(
//                        ListenBookActivity::class.java.name,
//                        arrayListOf(value)
//                    )
                },
                onLongClick = {
                    val value = item.url
                    Book.downLoad(value,item.preface)
                    ActivityRun.runOnUiThread {
                        NavScreen.BookmarkScreen.open("?pid=" + VirtualCommonItem.getId(VirtualCommonItem.Type.donwload)  )
                    }
                }
            )
            .align(Alignment.Start)

        ) {
            if(item.bookface.length>0) {
                Image(
                    painter = rememberGlidePainter(request = item.bookface),
                    contentDescription = "abc",
                    modifier = Modifier.size(50.dp)
                )
            }
            Text(
                text = item.preface, modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }
}

data class LoadJsonItem(
    @JsonProperty("bookface")
    var bookface: String = "",
    @JsonProperty("url")
    var url: String = "",
    @JsonProperty("preface")
    var preface: String = "",
    @JsonProperty("version")
    var version: Int = 0
) {
    companion object {
        val all: ArrayList<LoadJsonItem> = arrayListOf()
        fun fromMap(map: Map<String, Any>): LoadJsonItem {
            val objectMapper = ObjectMapper()
            val rt = objectMapper.convertValue(map, LoadJsonItem::class.java)
            all.add(rt)
            return rt
        }
    }
}

fun loadList(
    url: String = "http://www.ochess.cn/product/English/books.json",
    onOver: (lists: ArrayList<LoadJsonItem>) -> Unit
) {
    if (LoadJsonItem.all.size > 0) return
    val client = OkHttpClient()
    var docs = arrayListOf<LoadJsonItem>()
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val textData = response.body?.string() ?: ""
                val type: Type = object : TypeToken<Map<String, Any>>() {}.type
                val json: Map<String, Any> = Gson().fromJson(textData, type)
                val code = json.get("code")
                if (code != null && code.toString().toInt() > 0) {
                    throw TipException(json.get("msg").toString(), code)
                }
                val books: ArrayList<Map<String, Any>> =
                    json.get("books") as ArrayList<Map<String, Any>>
                books.forEach {
                    val item = LoadJsonItem.fromMap(it)
                    Log.d(TAG, "loadList: add " + item.preface)
                    docs.add(item)
                }
                onOver(docs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception, maybe update the UI to show an error message
        }
    }
}

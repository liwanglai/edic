package com.ochess.edict.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.main.components.Display.mtCnReplace
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.DateUtil
import com.ochess.edict.util.TimeStampScope
import java.lang.reflect.Type

class BookHistroy {
    data class Item(
        val id:Int = 0,
        val name:String="",
        val content:String = "",
        var inTime:Long = 0L
    )
    companion object {
        val sname = "BookHistorys"
        val wname = "BookHistorysLastWord"
        var lastWord = ""
        val key = mutableStateOf("")
        val date: MutableState<TimeStampScope?> = mutableStateOf(null)
        fun search(): List<Item> {
            return UserStatus.get{
                 val textData = it.getString(sname,"[]")
                 val type: Type = object : TypeToken<List<Item>>() {}.type
                 val bHistorys:List<Item> = Gson().fromJson(textData, type)
                return@get bHistorys
            }
        }

        fun add(book: BookItem) {
            val item = Item(book.id,book.name,book.info(),System.currentTimeMillis())
            val values = arrayListOf(item)
            val nowItems = search()
            //如果重复输入则更新创建时间
            if(nowItems.size>0 && nowItems[0].id==item.id){
                values.clear()
                nowItems[0].inTime=System.currentTimeMillis()
            }
            //如果大于100条则保持100条
            if(nowItems.size>100){
                nowItems.subList(0,nowItems.size-2)
            }
            values.addAll(nowItems)
            val value = Gson().toJson(values)
            UserStatus.set{
                it.putString(sname,value)
                return@set sname
            }
        }

        fun lastWord(word: String?=null,index:Int=0) :String{
            if(word!=null) {
                if(index==0 && lastWord.length==0) return ""
                lastWord = word
                UserStatus().set(wname, lastWord)
            }else{
                lastWord = UserStatus().getString(wname)
            }
            return lastWord
        }
    }

}


@Composable
fun HistoryBookScreen() {
    val key = BookHistroy.key.value
    val date = BookHistroy.date.value
    val list = BookHistroy.search().filter {
        var rt = true

        if(key.length>0) {
            rt = it.name.indexOf(key)>-1
        }
        if(rt && date!=null){
            rt = it.inTime> date.start && it.inTime<date.end
        }
        rt
    }
    Column {
        LazyColumn {
            itemsIndexed(list) { index, it ->
                val bColor = if (index % 2 == 0) Color.Gray else MaterialTheme.colorScheme.background
                val name = it.name.replace(Regex("\\..+$"),"")

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(bColor)
                    .clickable {
                        BookConf.setBook(it.id)
                        NavScreen.openHome(0)
                    }
                ) {
                    Text(text = name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = DateUtil.formatDateToDaysAgo(it.inTime))
                }
            }
        }
    }
}

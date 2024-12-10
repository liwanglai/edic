package com.ochess.edict.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ochess.edict.data.UserStatus
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.main.components.Display.mtCnReplace
import com.ochess.edict.presentation.navigation.NavScreen

class BookHistroy {
    class Item{
        val id = 0
        val name=""
        val content = ""
        val inTime = ""
    }
    companion object {
        val sname = "BookHistorys"
        fun search(): Array<Item> {
            return UserStatus.get{
                 arrayOf<Item>()
            }
        }

        fun add(book: BookItem) {
            val value = ""

            UserStatus.set{
                it.putString(sname,value)
                return@set sname
            }
        }
    }

}


@Composable
fun HistoryBookScreen() {
    val list = BookHistroy.search()
    Column {
        LazyColumn {
            itemsIndexed(list) { index, it ->
                var showMore by remember { mutableStateOf(false) }
                val bColor = if (index % 2 == 0) Color.Gray else MaterialTheme.colorScheme.background
                val name = mtCnReplace(it.name)
                val content = mtCnReplace(it.content)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(bColor)
                    .clickable {
                        showMore = !showMore
                    }
                ) {
                    Text(text = name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(it.inTime, Modifier)
                }
                if (showMore) {
                    Row(
                        Modifier.background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = content)
                    }
                }
            }
        }
    }
}

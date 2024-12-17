package com.ochess.edict.presentation.bookmark.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.R
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.BookItemType
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.bookItems
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.clickModel
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.pid
import com.ochess.edict.presentation.bookmark.data.BookMark.clickModelItem
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.ProgressDialog
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu

@OptIn(ExperimentalFoundationApi::class, ExperimentalUnitApi::class)
@Composable
@Preview
fun BookGrid() {
    val cells = if(ActivityRun.isHorizontalScreens()) GridCells.Adaptive(150.dp) else  GridCells.Fixed(4)
    LazyVerticalGrid (
        columns = cells,
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ){
        itemsIndexed(bookItems.filter{it.type == BookItemType.category}) { _, item ->
            Item2(item)
        }
        if(pid==0) {
            itemsIndexed(VirtualCommonItem.all) { _, item ->
                Item2(item)
                if (pid == item.id) {
                    BookMarkEvent.onCommonItemClick(item)
                }
            }
        }
        itemsIndexed(bookItems.filter{it.type == BookItemType.article}) { _, item ->
            Box {
                Item2(item)
                //初始化mp3文件的进度条
                if (!item.inited ) {
//                    Text(
//                            item.initStatusText, color = Color.Red, modifier = Modifier.align(Alignment.TopCenter)
//                            .padding(top = 10.dp)
//                    )

                    if (ProgressDialog.size.value>0 && BookMarkEvent.openItem!=null && BookMarkEvent.openItem!! == item) {
                        Text(text = "" + ProgressDialog.pos.value + "/" + ProgressDialog.size.value,
                            fontSize = 6.sp,
                            textAlign = TextAlign.Center
                        )
                        LinearProgressIndicator(
                            //0.0表示没有进度，1.0表示已完成进度
                            progress = ProgressDialog.progress.value,
                            modifier = Modifier.padding(10.dp).align(Alignment.BottomCenter),
                            color = Color.Green,
//                            backgroundColor = Color.Gray
                        )
                    }
                }
            }
        }
        itemsIndexed(bookItems.filter{it.type == BookItemType.word}) { index, item ->
            Item2(item)
            item.index = index
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalUnitApi::class)
@Composable
fun Item2(item: Any){
        val isBi = item::class.java.equals(BookItem::class.java)
        val itemBi = if(isBi) item as BookItem else null
        val menu = if(isBi) MPopMenu.categoryEditMenu(item as BookItem) else null
        val bgc = if(BookMarkEvent.bookId>0 && isBi && itemBi!!.type ==BookItemType.article && itemBi!!.id == BookMarkEvent.bookId)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.background
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp)
                    .wrapContentHeight()
                    .combinedClickable(
                        onClick = {
                            if(isBi) {
                                BookMarkEvent.ItemOnClick(itemBi!!)
                            }else{
                                BookMarkEvent.onCommonItemClick(item as VirtualCommonItem)
                            }
                        },
                        onLongClick = {
                            if(menu!=null) {
                                BookMarkEvent.ItemonLongClick(menu)
                            }
                        }
                    )
                    .background(bgc)
            ) {
                if(isBi) {
                    if (clickModel == clickModelItem.isSelect) {
                        RadioButton(selected = itemBi!!.isSelect, onClick = {
                            itemBi!!.changeSelecte()
                        })
                    }
                }
                val ico = if(isBi) itemBi!!.ico else (item as VirtualCommonItem).ico
                val name = if(isBi) itemBi!!.name else mt((item as VirtualCommonItem).name)
                if(ico > 0) {
                    Image(
                        painter = painterResource(id = ico),
                        name,
                        modifier = Modifier
                            .size(70.dp)
                    )
                   // Spacer(Modifier.height(5.dp))
                }

                if(isBi && itemBi!!.type == BookItemType.word)
                {
                    Text(itemBi!!.name, modifier = Modifier)
                    Spacer(Modifier.width(5.dp))
                    Text(itemBi!!.content!!)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        itemBi!!.delete()
                        BookMarkEvent.Run.upBookItems()
                    }, modifier = Modifier.weight(.5f)) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }else {
                    Text(name.replace(Regex("_ISBN.+|\\.\\w+$"),""), fontSize = 10.sp)
//                    Spacer(modifier = Modifier.weight(1f))
//                    Text(
//                        item.upTime ?: "",
//                        color = Color.Gray,
//                        fontSize = TextUnit(10f, TextUnitType.Sp)
//                    )
                }
                if(menu!=null) {
                    menu.add()
                }
        }
}
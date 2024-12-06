package com.ochess.edict.presentation.bookmark.components

import android.os.Handler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ochess.edict.R
import com.ochess.edict.presentation.bookmark.BookMarkEvent
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.BookItemType
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu


import com.ochess.edict.presentation.bookmark.data.BookMark.clickModelItem
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.bookItems
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.clickModel
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.pid

@Composable
@Preview
fun BookList() {
//    if(pid==0) {
//        val bookmarkViewModel: BookmarkViewModel = GlobalVal.bookmarkViewModel
//        val wList = bookmarkViewModel.getAll().collectAsState().value
//        wList.forEach {
//            val item = BookItem.build(it)
//            bookItems.add(item)
//        }
//    }


    var cells = if(ActivityRun.isHorizontalScreens()) GridCells.Adaptive(100.dp) else  GridCells.Fixed(4)
//    LazyVerticalGrid (
//        cells = cells,
//        modifier = Modifier
//            .padding(horizontal = 16.dp)
//    ){
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
//        if(cList.size==0){
//            items(arrayOf(BookItem(0))){
//                Item(0, it)
//            }
//        }
        itemsIndexed(bookItems.filter{it.type == BookItemType.category}) { index, item ->
            Item(index, item)
        }
        itemsIndexed(VirtualCommonItem.all) { index, item ->
            ItemCommon(item)
        }

        itemsIndexed(bookItems.filter{it.type == BookItemType.article}) { index, item ->
            Item(index, item)
        }
        itemsIndexed(bookItems.filter{it.type == BookItemType.word}) { index, item ->
            Item(index, item)
            item.index = index
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ItemCommon(item: VirtualCommonItem) {
    if(pid>0) {
        return
    }else if(pid<0) {
        if(pid == item.id) {
            BookMarkEvent.onCommonItemClick(item)
        }
        return
    }
    val name = item.name
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .wrapContentHeight()
            .clickable {
//                pid = item.id
                BookMarkEvent.onCommonItemClick(item)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = item.ico),
                name,
                modifier = Modifier
                    .size(30.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(text = name)
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalUnitApi::class)
@Composable
fun Item(index:Int, item:BookItem){
        val menu = MPopMenu.categoryEditMenu(item)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .wrapContentHeight()
                .combinedClickable(
                    onClick = { BookMarkEvent.ItemOnClick(item) },
                    onLongClick = {
                        BookMarkEvent.ItemonLongClick(menu)
                    }
                ),
            shape = RoundedCornerShape(8.dp),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.surface)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                if(clickModel == clickModelItem.isSelect) {
                    RadioButton(selected = item.isSelect, onClick = {
                        item.changeSelecte()
                    })
                }
                if(item.ico>0) {
                    Image(
                        painter = painterResource(id = item.ico),
                        item.name,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                }
                if(item.type == BookItemType.word)
                {
//                    Box {
//                        WordItem(index, item, {
//                            BookMarkEvent.ItemOnClick(item)
//                        }, {
//                            bookmarkViewModel.deleteBookmark(item)
//                        })
//                    }
                    Text(item.name, modifier = Modifier
//                            .width(300.dp)
                        //.alpha(0f)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(item.content!!)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        item.delete()
                        BookMarkEvent.Run.upBookItems()
                    }, modifier = Modifier.weight(.5f)) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }else {
                    Text(item.name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        item.upTime ?: "",
                        color = Color.Gray,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                }
            }
            menu.add()
        }
}
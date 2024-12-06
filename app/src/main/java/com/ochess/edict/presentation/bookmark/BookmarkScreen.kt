package com.ochess.edict.presentation.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.model.Category
import com.ochess.edict.presentation.bookmark.components.BookGrid
import com.ochess.edict.presentation.bookmark.components.BookList
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.BookMark
import com.ochess.edict.presentation.main.components.AddButton
import com.ochess.edict.presentation.main.components.Confirm
import com.ochess.edict.presentation.main.components.InputDialog
import com.ochess.edict.presentation.main.components.Prompt
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu
import com.ochess.edict.presentation.main.extend.MText as Text

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkScreen() {
    GlobalVal.clipboardManager = LocalClipboardManager.current
    val titleMenu by remember {
        mutableStateOf(MPopMenu(arrayListOf()))
    }
    var title by remember {
        mutableStateOf("Bookmarks")
    }
    remember {
        // 显示确认对话框
        BookMark.onChangePid { pid ->
            if (GlobalVal.pid != pid) {
                BookMarkEvent.Run.upBookItems()
                GlobalVal.pid = pid
            }
            if (pid == 0) {
                title = "Books"
                titleMenu.upItems(arrayListOf())
                return@onChangePid
            }
            val ps = Category.getParents(pid)
            val nowMenu = ps[0]
            val bmChild = arrayListOf<MPopMenu.dataClass>()
            title = nowMenu.name
            ps.subList(1, ps.size).forEach {
                bmChild.add(MPopMenu.dataClass(it.name, value = it.id))
            }
            bmChild.add(MPopMenu.dataClass("Books", value = 0))
            titleMenu.upItems(bmChild)

            var topCount = 0
            ActivityRun.onBackPressed {
                if (pid != 0) {
                    BookMark.changePid(nowMenu.pid)
                    topCount = 0
                } else {
                    topCount++
                }
                pid == 0 && topCount == 1
            }
        }
        BookMark.onChangePid()
        BookMark.pid
    }
    Confirm.add()
    Prompt.add()
    Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .combinedClickable(
                                onClick={
                                    titleMenu.show{ _, v->
                                        BookMark.changePid(v.value as Int)
                                    }
                                },
                                onLongClick = {
                                    GlobalVal.isSearchVisible.value = true
                                }
                            )
                    )
                    titleMenu.add()
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    when(BookMark.clickModel){
                        BookMark.clickModelItem.isSelect -> {
                            Button(onClick = { BookMarkEvent.onCheckAll(BookMark.bookItems) }) {
                                Text("全选")
                            }
                            Spacer(Modifier.width(5.dp))
                            Button(onClick = {
                                BookMark.clickModel = BookMark.clickModelItem.isCanCopy
                                BookMarkEvent.cutData = BookMark.bookItems.filter { it.isSelect }

                                if(BookMark.pid==-1) BookMark.changePid(0)
                            }) {
                                Text("剪切")
                            }
                            Spacer(Modifier.width(5.dp))
                            Button(onClick = {
                                BookMarkEvent.onDeleteItems(BookMark.bookItems.filter { it.isSelect })
                            }) {
                                Text("删除")
                            }
                            Spacer(Modifier.width(5.dp))
                        }
                        BookMark.clickModelItem.isCanCopy -> {
                            Button(onClick = { BookMarkEvent.onPaste(BookMark.pid) }) {
                                Text("粘贴")
                            }
                            Spacer(Modifier.width(5.dp))
                            Button(onClick = { BookMark.clickModel = BookMark.clickModelItem.click }) {
                                Text("取消")
                            }
                            Spacer(Modifier.width(5.dp))
                        }
                        else ->{

                        }
                    }
                    AddButton(
                        menu =MPopMenu.BookAddMenu(),
                        onLongClick ={
                            InputDialog.show()
                        },
                        onClick = {
                            BookMarkEvent.AddButtonOnLongClick(it)
                        }
                    )
                }
                Row {
                    if(BookMark.pid==-1) {
                        BookList()
                    }else {
                        BookGrid()
                    }
                }
            }
    }
    InputDialog.add("创建分类","输入你的分类名字或文章名字:",{
        if(InputDialog.eObj!=null) {
            val item = InputDialog.eObj as BookItem
            item.save(content=it)
            BookMarkEvent.Run.upBookItems()
        }else {
            BookMarkEvent.onAdd(it, BookMark.pid)
        }
    })

//    InfoDialog.add()
}

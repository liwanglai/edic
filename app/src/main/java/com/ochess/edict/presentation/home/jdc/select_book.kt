package com.ochess.edict.presentation.home.jdc

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.ochess.edict.R
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Book
import com.ochess.edict.presentation.home.PAGE_FROM_HOME
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.layouts
import okhttp3.internal.notify

@Composable
fun select_book(){
    var canEdit = remember { mutableStateOf( false) }
    var reDrawCount by remember { mutableStateOf( 0) }
    fun reload() {
        reDrawCount++
    }
    if(reDrawCount >-1);
    LayoutJdc.view(layouts.books){
        val gridLayoutManager = GridLayoutManager(it.context, 5, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.canScrollVertically()

        it.apply {
            //返回按钮
            findViewById<ImageButton>(R.id.back).setOnClickListener {
                viewMode = MenuConf.mode.jdc_select
                LayoutJdc.goHome()
            }
            //编辑的删除按钮
            findViewById<TextView>(R.id.tv_edit).setOnClickListener{
                canEdit.value = !canEdit.value
                reload()
            }
            ////列表 新增与删除
            findViewById<RecyclerView>(R.id.mRecyclerView).apply {
                layoutManager = gridLayoutManager
                adapter = Book.getHaveBooks(R.layout.item_gridview) { it, vItem ->
                    it.apply {
                        //添加按钮
                        val bookFace = findViewById<ImageView>(R.id.file_image)
                        findViewById<ImageView>(R.id.add_book).apply {
                            visibility = View.GONE
                            if (vItem.id == 0) {
                                visibility = View.VISIBLE
                                setImageResource(R.drawable.add_book)
                                bookFace.visibility = View.GONE
                            }
                        }
                        //书本名字
                        findViewById<TextView>(R.id.file_name).text = vItem.name
                        //删除按钮
                        findViewById<ImageView>(R.id.delete_img).apply {
                            visibility = if (canEdit.value && vItem.id>0) View.VISIBLE else View.INVISIBLE
                            setOnClickListener {
                                Article.delete(vItem.id)
                                reload()
                            }
                        }
                        //正在学习书本标记
                        if (BookConf.instance.id == vItem.id && vItem.id>0) {
                            findViewById<ImageView>(R.id.studying).visibility = View.VISIBLE
                        }

                        //书籍选中
                        findViewById<View>(R.id.frame).setOnClickListener {
                            if (vItem.id == 0) {
                                NavScreen.BookmarkScreen.open("?pid=-2")
                            } else {
                                BookConf.setBook(vItem, true)
                                NavScreen.openHome(0)
                            }
                        }
                    }

                }
            }

        }
    }
}

package com.ochess.edict.presentation.home.jdc

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookmarkViewModel
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.Companion.goHome
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.click
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.hide
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.id
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.text
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

var canManage by mutableStateOf(false)
@Composable
fun new_word(){
    val bookmarkViewModel: BookmarkViewModel = hiltViewModel()
    val bookmarks = bookmarkViewModel.bookmarks.asStateFlow()
    bookmarkViewModel.getAll()

    LayoutJdc.view(LayoutJdc.layouts.bookmark){
        //返回按钮
        click(R.id.back){
            goHome()
        }
        //管理
        click(R.id.manage) {
            canManage = !canManage
            NavScreen.openJdc(4)
        }

        id<ListView>(R.id.listView).adapter = getWords(bookmarks,R.layout.listview_item){index,view,model ->
            view.apply {
//                if(index%2 ==0) setBackgroundColor(getResources().getColor(R.color.color_switch_button_bg_checked))
                if(index%2 ==1) setBackgroundColor(Color.LTGRAY)
                findViewById<TextView>(R.id.number).text = (index+1).toString()
                findViewById<TextView>(R.id.word).text = model.word
                findViewById<TextView>(R.id.wordMean).text = model.ch
                findViewById<TextView>(R.id.delete_view).apply {
                    visibility = if(canManage) View.VISIBLE else View.GONE
                    setOnClickListener {
                        GlobalVal.bookmarkViewModel.deleteBookmark(model)
                        NavScreen.openJdc(4)
                    }
                }

            }
        }

        var total=""
        if(bookmarks.value.size>0){
            total = "("+bookmarks.value.size + ")"
            hide(R.id.tv_null_data)
        }else{
            hide(R.id.listView)
        }
        text(R.id.total,total)
    }
}

fun getWords(
    bookmarks: StateFlow<List<WordModel>>, layoutId:Int,
    onBindViewHolder:(Int, ViewGroup, WordModel)->Unit ): ListAdapter {
    var ap = LayoutJdc.Views.Adapter(ActivityRun.context(), layoutId, { it, vItem ,pos->
        onBindViewHolder(pos,it, vItem as WordModel)
    })
    if(bookmarks.value.size>0) {
        ap.setItems(bookmarks.value as ArrayList<Any>)
    }
    return ap
}
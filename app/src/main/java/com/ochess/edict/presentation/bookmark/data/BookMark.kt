package com.ochess.edict.presentation.bookmark.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class BookMark {
    //当前分类id
    enum class clickModelItem { click, isSelect, isCanCopy }

    companion object {
        private var runOnece:Boolean = false
        private object events {
            var onChangePid:(pid:Int)->Unit = {}
        }
        fun onChangePid() {
            if(!runOnece) {
                events.onChangePid(pid)
                runOnece= true
            }
        }
        fun onChangePid(eventFun: (pid:Int) -> Unit) {
            events.onChangePid = eventFun
        }

        fun changePid(id: Int) {
            pid = id
            events.onChangePid(pid)
        }

        fun setBookItems(bi: ArrayList<BookItem>) {
            MainScope().launch {
                bookItems.clear()
                bookItems.addAll(bi)
            }
        }

        var pid =0
        var clickModel by mutableStateOf(clickModelItem.click)
        var bookItems = mutableStateListOf<BookItem>()
            private set
    }
}
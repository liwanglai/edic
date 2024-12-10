package com.ochess.edict.presentation.history

import androidx.compose.runtime.Composable
import com.ochess.edict.view.MPopMenu

/**
 * 单词无限极的历史记录
 */
class HistoryWords {
    companion object {
        val menu: MPopMenu = MPopMenu(arrayListOf<MPopMenu.dataClass>())

        @Composable
        fun add(word: String) {
            val index =menu.items.size-1
            menu.items.add(MPopMenu.dataClass(word,word,value=index))
            return menu.add()
        }

        fun slice(index: Int) {
            val items = menu.items.subList(0,index)
            menu.items.clear()
            menu.items.addAll(items)
        }

        fun reset() {
            menu.items.clear()
        }

    }
}
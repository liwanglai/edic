package com.ochess.edict.presentation.history

import androidx.compose.runtime.Composable
import com.ochess.edict.data.GlobalVal.wordViewModel
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.view.MPopMenu

/**
 * 单词无限极的历史记录
 */
class HistoryWords {
    companion object {
        val menu: MPopMenu = MPopMenu(arrayListOf<MPopMenu.dataClass>())
        var size=0
        fun add(wordModel:WordModel?){
            if(wordModel == null) return
            val word = wordModel.word
            val index =menu.items.size
            if(menu.items.size==0 || menu.items[menu.items.size-1].name!=word) {
                menu.items.add(MPopMenu.dataClass(word, word, value = index))
            }
        }
        @Composable
        fun menu() {
            size = menu.items.size
            if(size>1) {
                menu.add()
            }
        }
        //配合返回后退事件
        fun pop(){
            if(menu.items.size>0) {
                menu.items.removeAt(menu.items.size - 1)
            }
            if(menu.items.size>0) {
                val last = menu.items.last()
                wordViewModel.searcher(last.name)
            }
            size = menu.items.size
        }

        //截取 配合单词选择事件
        fun slice(index: Int) {

            val items = menu.items.subList(0,index).map{it}
            menu.items.clear()
            menu.items.addAll(items)
        }

        fun reset() {
            menu.items.clear()
            size= 0
        }

    }
}
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
        var sliceDiscard= arrayListOf<WordModel>()
        fun add(wordModel:WordModel?){
            if(wordModel == null || wordModel.word.length==0) return
            if(sliceDiscard.size>0 && wordModel.word in sliceDiscard.map{it.word}) return
            val word = wordModel.word
            val index =menu.items.size
            if(menu.items.size==0 || menu.items[0].name!=word) {
                menu.items= arrayListOf( MPopMenu.dataClass(word, word, value = wordModel,index=index)).apply {
                    addAll(menu.items)
                }
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
                menu.items.removeAt(0)
            }
            if(menu.items.size>0) {
                val last = menu.items.first()
                wordViewModel.searcher(last.name)
            }
            size = menu.items.size
        }

        //截取 配合单词选择事件
        fun slice(index: Int) {
            val len = menu.items.size-index
            val items = menu.items.subList(len,size).map{it}
            sliceDiscard.addAll(menu.items.subList(0,len-1).map{it.value as WordModel})
            menu.items.clear()
            menu.items.addAll(items)
        }

        fun reset() {
            menu.items.clear()
            sliceDiscard.clear()
            size= 0
        }

    }
}
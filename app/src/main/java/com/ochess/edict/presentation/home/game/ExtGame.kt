package com.ochess.edict.presentation.home.game

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.model.Word
import com.ochess.edict.data.model.WordExtend
import com.ochess.edict.presentation.home.game.ext.WordExtGame

/**
 * 词根扩展游戏
 */
@Composable
fun ExtGame() {

    val book = BookConf.instance
    val eWord = remember{ mutableStateOf( WordExtend(book.word))}
    fun nextWord(): Boolean {
        if(eWord.value.eSize() >0){
           return true
        }
        while(book.next()){
            eWord.value = WordExtend(book.word)
            if(eWord.value.eSize() >0) return true
        }
        return false
    }
    val name = book.word
    val wordModel = GlobalVal.wordViewModel.wordState.collectAsState()
    val word =wordModel.value.wordModel?.word
    if(word!=null) {
        Text(word, modifier = Modifier.padding(10.dp,20.dp))
        eWord.value = WordExtend(word)
        while(!nextWord());
    }
    val exWords = Word.gets(eWord.value.extword)
    if(exWords!=null) {
        LazyColumn {
            items(1) {
                WordExtGame.out(word = name, points = exWords.filter { it.word.indexOf(name) > -1 })
            }
        }
    }
}
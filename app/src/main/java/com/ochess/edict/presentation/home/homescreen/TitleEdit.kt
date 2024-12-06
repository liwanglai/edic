package com.ochess.edict.presentation.home.homescreen

import android.os.Handler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ochess.edict.presentation.home.TTSListener

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun TitleEdit(word: String="word", title: String="中文意思", onOver: (next:Boolean) -> Unit={}) {
    val text = rememberSaveable { mutableStateOf("") }
    val beforeText = rememberSaveable { mutableStateOf("") }
    val fo = remember {
        FocusRequester()
    }
    val context = LocalContext.current
    val ttsListener by remember {
        mutableStateOf(TTSListener( context) {})
    }
    var label = title
    if(beforeText.value == "") {
        label +="(Input enter key to exited Edit Model)"
    }
    TextField(
        value = text.value,
        onValueChange = {
            var newT = text.value
            val lastE = if (it.isNotEmpty())  it[it.length-1] else '\b'
            when(lastE){
                ' ' ->
                    if(newT == word.substring(0,it.length-1)) {
                        newT = text.value + word[it.length - 1]
                    }else{
                        var n =it.length-1
                        while(n-- >0 && !word.startsWith(it.substring(0,n)));
                        newT = word.substring(0,n)
                    }
                '\n' ->
                    onOver(false)
                else ->{
                    newT +=lastE
                    if(newT != it) {
                        newT = it
                    }else if(word.startsWith(newT) ){
                        ttsListener.speak(lastE.toString())
                    }
                }
            }
            text.value = newT
            if(text.value == word) {
//                ttsListener.play(title)
                Thread.sleep(500)
                onOver(true)
            }
        },
        label = { Text(title) },
        modifier = Modifier.combinedClickable(
            onClick = {},
        )
        .focusable(true)
        .focusOrder(fo)
        .onFocusChanged {

        }
    )
    if(beforeText.value != word) {
        beforeText.value = word
        Handler().postDelayed({
            fo.requestFocus()
            ttsListener.speak(word)
        },100)
    }
}
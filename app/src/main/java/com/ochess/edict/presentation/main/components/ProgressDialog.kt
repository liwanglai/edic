package com.ochess.edict.presentation.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

class ProgressDialog {
    companion object {
        val dialogState = mutableStateOf(false)
        val progress = mutableStateOf(0f)
        val pos = mutableStateOf(0)
        val size = mutableStateOf(0)

        @Composable
        fun add() {
            if(dialogState.value) {
                Dialog(onDismissRequest = { dialogState }) {
                    //圆形进度条--无限循环
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            //0.0表示没有进度，1.0表示已完成进度
                            progress = progress.value,
                            modifier = Modifier.padding(10.dp),
                            color = Color.Yellow,
                        )
                        Spacer(modifier = Modifier.requiredHeight(10.dp))
                        Text(text = "" + pos.value + "/" + size.value)
                    }
                }
            }
        }

        fun Progress(i: Int, s: Int) {
            pos.value=i
            size.value = s
            progress.value = i.toFloat()/s
        }
        fun show(){
            dialogState.value = true
        }
        fun close() {
            dialogState.value=false
        }
    }


}
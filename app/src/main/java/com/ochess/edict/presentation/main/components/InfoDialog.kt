package com.ochess.edict.presentation.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Dialog

class InfoDialog() {
    companion object {
        val dialogState = mutableStateOf(false)
        var content: @Composable () -> Unit ={}
        @Composable
        fun add() {
            if(dialogState.value) {
                Dialog(onDismissRequest = { dialogState.value=false }) {
                    content()
                }
            }
        }
        fun show(ct: @Composable () -> Unit){
            content = ct
            dialogState.value = true
        }
        fun close() {
            dialogState.value=false
        }
    }
}
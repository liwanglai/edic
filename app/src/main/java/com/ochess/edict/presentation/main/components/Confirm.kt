package com.ochess.edict.presentation.main.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class Confirm {
    constructor(title0:String,msg0:String,onOk0:()->Unit,onCancel0:(()->Unit)?=null){
        title = title0
        msg = msg0
        onOk = onOk0
        onCancel = onCancel0
    }
    companion object {
        var onOk: () -> Unit ={}
        var onCancel: (() -> Unit)? = null
        var title: String = "确认"
        var msg: String = "你确定要删除吗？"
        var showDialog by mutableStateOf(false)

        @Composable
        fun add() {

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(title) },
                    text = { Text(msg) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onOk()
                                // 执行确认操作
                                showDialog = false
                            }
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // 执行取消操作
                                showDialog = false
                                if(onCancel!=null){
                                    onCancel?.invoke()
                                }
                            }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
        }

        fun show(text:String="",okDo:()->Unit){
            if(text.length>0) msg = text
            onOk =okDo
            showDialog = true
        }
    }

}
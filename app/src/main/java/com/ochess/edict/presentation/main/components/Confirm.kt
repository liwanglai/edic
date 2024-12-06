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
    companion object {
        var onOk: () -> Unit ={}
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
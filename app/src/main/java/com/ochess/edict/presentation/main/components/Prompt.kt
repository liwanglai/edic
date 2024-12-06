package com.ochess.edict.presentation.main.components

import android.os.Handler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ochess.edict.view.beforeClikableButton.Companion.tv

class Prompt() {
  companion object {
      var title: String by mutableStateOf("重命名")
      var msg by mutableStateOf("请输入新名:")
      var showDialog by mutableStateOf(false)
      var textValue by mutableStateOf(TextFieldValue(""))
      var overDo:(tv:String)->Unit = {}
    @Composable
    fun add() {
        if (showDialog) {
            var textFocus = FocusRequester()
            Handler().postDelayed({
                textFocus.requestFocus()
            },200)

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(title) },
                text = {
                    Column {
                        Text(msg)
                        TextField(value = textValue,
                            onValueChange = {
                                textValue = it
                            },modifier= Modifier
                                .focusable()
                                .focusOrder(textFocus)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        val text = textValue.text
                                        textValue = textValue.copy(
                                            selection = TextRange(0, text.length)
                                        )
                                    }
                                }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onOver(textValue.text)
                            textValue = TextFieldValue("")
                            // 执行确认操作
                            showDialog = false
                        }
                    ) {
                        Text("确定")
                    }
                }
            )
        }

    }

      private fun onOver(textValue: String) {
        overDo(textValue)
      }

      fun show(nameOld: String, onOver: (newVal:String) -> Unit) {
          textValue = TextFieldValue(nameOld)
          showDialog = true
          overDo = onOver
      }
    }
}
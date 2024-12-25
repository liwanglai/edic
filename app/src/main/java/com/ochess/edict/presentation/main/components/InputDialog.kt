package com.ochess.edict.presentation.main.components

import android.os.Handler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.config.MenuConf.Companion.bottom
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.Display.mtCnReplace
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil

class InputDialog {

    companion object{
        var showDialog by mutableStateOf(false)
        var textContent by mutableStateOf("")
        var onSave:(tx:String)->Unit = {}
        var eObj:Any? = null


        @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
        @Composable
        @Preview
        fun add(title:String, des:String="", saveDo: ((tx: String) -> Unit)? =null,cancelDo:()->Unit ={}){
            if(saveDo!=null) onSave = saveDo
            if(showDialog) {
                val textFocus = remember { FocusRequester() }
                if(MenuConf.type().value.equals(bottom)) {
                    Handler().postDelayed({
                        textFocus.requestFocus()
                    }, 500)
//                    (ActivityRun.onKeyBoardStatusChange {
//                        if (!it) {
//                            showDialog = false
//                            cancelDo()
//                        }
//                    })
                }
                AlertDialog(
                    onDismissRequest = {
//                        showDialog = false
//                        cancelDo()
                    },
                    title = {
                        Row {
                            Text(mt(title))
                            Spacer(Modifier.weight(1f))
                            CloseButton(onClick = {
                                showDialog = false
                                cancelDo()
                            })
                        }
                    },
                    text = {
                        Column {
                            Text(mtCnReplace(des))
                            Spacer(Modifier.height(16.dp))
                            TextField(
                                value = textContent,
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
//                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                onValueChange = {
                                    textContent = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()

                                    .height(350.dp)
                                    .background(Color.White)
                                    .border(1.dp, Color.Black)
                                    .focusable()
                                    .focusRequester(textFocus)
                            )
                            Text(mt("从文件"),Modifier.clickable {
                                ActivityRun.onKeyBoardStatusChange {
                                }
                                FileUtil.pickFile(onReadFile = { data, file, uri->
                                    textContent = data
                                })
                            })
                            Button(
                                onClick = {
                                    onSave(textContent)
                                    textContent=""
                                    showDialog = false
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                            ) {
                                Text(mt("Save"))
                            }

                        }
                    },
                    confirmButton = { }
                )
            }
        }

        fun show(eObj:Any?=null,text:String="") {
            this.eObj = eObj
            textContent=text
            showDialog=true
        }

        fun setText(it: String) {
            textContent = it
        }

    }
}
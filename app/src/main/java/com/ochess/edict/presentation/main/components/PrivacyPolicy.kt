package com.ochess.edict.presentation.main.components

import android.os.Process
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ochess.edict.presentation.main.components.Confirm.Companion.msg
import com.ochess.edict.presentation.main.components.Confirm.Companion.onCancel
import com.ochess.edict.presentation.main.components.Confirm.Companion.onOk
import com.ochess.edict.presentation.main.components.Confirm.Companion.showDialog
import com.ochess.edict.presentation.main.components.Confirm.Companion.title
import com.ochess.edict.presentation.main.extend.HtmlView
import com.ochess.edict.presentation.main.extend.MText as Text

@Composable
fun PrivacyPolicy(onOk:()->Unit){
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("隐私政策") },
        text = { HtmlView("欢迎使用“电子词典”，我们非常重视您的隐私保护，在您使用本应用之前，请仔细阅读<a href='https://gitee.com/kelioc/edic/blob/master/pp.html'>《》</a>我们将按照您同意的条款使用您的个人信息，以便为您提供服务",0.7f) },
        confirmButton = {
            Button(
                onClick = {
                    onOk()
                    // 执行确认操作
                    showDialog = false
                }
            ) {
                Text("同意")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    // 执行取消操作
                    showDialog = false
                    Process.killProcess(Process.myPid())
                }
            ) {
                Text("不同意")
            }
        }
    )
}
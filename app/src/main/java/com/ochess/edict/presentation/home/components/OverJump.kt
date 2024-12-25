package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.model.Book
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.main.components.CloseButton
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.InfoDialog
import com.ochess.edict.presentation.main.components.InputDialog.Companion.showDialog

@Composable
@Preview
fun OverJump(cancelDo:()->Unit={InfoDialog.close()}){
    Card() {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(mt("已经是最后一个了"), fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            CloseButton(onClick = {
                showDialog = false
                cancelDo()
            })
        }
        Row(Modifier.padding(20.dp)) {
            Button({
                Book.reload()
                cancelDo()
            }){
                Text(mt("重新开始"))
            }

            Spacer(Modifier.weight(1f))
            val nc = Book.nextChapter()
            if(nc!=null){
                Button({
                    Book.nextChapter(true)
                    cancelDo()
                }){
                    Text(mt("下一个章节"))
                }
            }else {
                Button({
                    viewMode = MenuConf.mode.chapterPage
                    cancelDo()
                }) {
                    Text(mt("章节选择"))
                }
            }

        }

    }
}
package com.ochess.edict.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.R

class Test {
    var visible by  mutableStateOf(true)
    var offset:DpOffset by mutableStateOf( DpOffset(0.dp,0.dp) )
    var items = arrayListOf("aa","bb","cc")
    @Composable
    fun test() {

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "123",modifier = Modifier
                .pointerInput(Unit){
                    detectTapGestures(
                        onTap = {
                            offset = DpOffset(
                                     300.dp,
                                     50.dp
                            )
                            visible=true
                        }
                    )
                }
            )
            DropdownMenu(expanded = visible,
                offset = offset,
                onDismissRequest = {
                    visible = false
                },
                modifier = Modifier.border(1.dp, Color.Black)
            ) {
                items.forEach { item->
                    DropdownMenuItem(
                        onClick = {},
                        content = {
                            Text(text = item)
                        }
                    )
                }
            }
        }
    }

@OptIn(ExperimentalUnitApi::class)
@Composable
@Preview(showBackground = true)
    fun item(){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .alpha(1f)
        ) {
            RadioButton(selected = false, onClick = {})
            Image(
                painter = painterResource(id = R.drawable.category) ,
                "test",
                modifier = Modifier
                    .size(50.dp)
            )
            Text("文件夹")
            Spacer( modifier = Modifier.weight(1f))
            Text("2024-05-31", color = Color.Gray,fontSize= TextUnit(10f, TextUnitType.Sp))
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    //@Preview
    fun add(){
        IconButton(onClick = {  }, modifier = Modifier) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .border(1.dp, Color.Gray, RoundedCornerShape(50.dp))
                    .size(50.dp)
            )
        }
    }
    @Composable
    fun MyAlpha() {
        Row {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .size(50.dp)
            ) {

            }

            Box(
                modifier = Modifier
                    .alpha(0.2f)
                    .background(Color.Red)
                    .size(50.dp)
            ) {

            }
        }
    }

    @Composable
    @Preview
    fun eds(){

            IconButton(
                onClick = {
                },
                modifier = Modifier.size(15.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.edinhos),
                    contentDescription = "爱丁浩斯学习",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "99",
                    fontSize = 3.sp,
                    color = Color.Black,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.Red)
                        .padding(1.dp)
                        .size(7.dp)
                        .alpha(0.9f)
                )
            }
    }
}
package com.ochess.edict.presentation.main.extend

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.ochess.edict.presentation.main.components.Display.mt

@Composable
fun MText(text: String,
          modifier: Modifier = Modifier,
          color: Color = Color.Unspecified,
          style: TextStyle = LocalTextStyle.current
          ){
    val textNew = if(text.endsWith(":")) mt(text.trim(':')) else mt(text)
    Text(textNew,modifier,color,style=style)
}
package com.ochess.edict.presentation.main.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.FlowRow as FlowRow0

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(modifier:Modifier=Modifier,content: @Composable RowScope.()->Unit){
    FlowRow0 (modifier = modifier,content=content)
}
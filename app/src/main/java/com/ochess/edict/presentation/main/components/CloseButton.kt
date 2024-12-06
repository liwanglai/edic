package com.ochess.edict.presentation.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ochess.edict.R

@Composable
fun CloseButton(
    onClick:()->Unit,
    color:Color= Color.Red,
) {
    IconButton(onClick = {
            onClick()
        },
        modifier = Modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = null,
            tint = color
        )
    }
}
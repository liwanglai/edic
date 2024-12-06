package com.ochess.edict.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ochess.edict.view.MPopMenu

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddButton(
    onClick: (m:MPopMenu)->Unit,
    onLongClick: (m:MPopMenu)->Unit = {},
    size:Dp  = 20.dp,
    menu:MPopMenu = MPopMenu.Unspecified,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    //fun IconButton(
    Box(contentAlignment = Alignment.Center,
            modifier = Modifier
//            .clickable(
//                onClick = onClick,
//                enabled = enabled,
//                role = Role.Button,
//                interactionSource = interactionSource,
//                indication = rememberRipple(bounded = false, radius = 24.dp)
//            )
            .combinedClickable(
                onClick = {
                    onClick(menu)
                },
                onLongClick = {
                    onLongClick(menu)
                })
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(size))
                    .padding(5.dp)
                    .size(size)
            )
        })
        menu.add()
    }
}


package com.ochess.edict.view

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.DpOffset

// https://blog.csdn.net/mp624183768/article/details/129959549
@Composable
fun PopMenu(
    visible: MutableState<Boolean>,
    offset: MutableState<DpOffset>,
    items: HashMap<String, String>,
    onItemClick:(k:String,v:String) -> Unit
) {
    DropdownMenu(expanded = visible.value,
        offset = offset.value.copy(y = offset.value.y),
        onDismissRequest = {
            visible.value = false
        }) {

        for(item in items) {
            DropdownMenuItem(
                onClick = {
                    onItemClick(item.key,item.value)
                    visible.value = false
                },
                content = {
                    Text(item.key)
                },
            )
        }
    }
}
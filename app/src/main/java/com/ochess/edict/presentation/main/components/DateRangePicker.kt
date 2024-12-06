package com.ochess.edict.presentation.main.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun DateRangePicker(
    startDate: MutableState<String>,
    endDate: MutableState<String?>,
    onStartDateChange: (Date) -> Unit,
    onEndDateChange: (Date) -> Unit
) {
    Column {
        DatePickerDialog.add()
        // 开始日期输入
        OutlinedTextField(
            value = startDate.value?.toString() ?: "",
            onValueChange = { text ->
                if(text.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                    val date = SimpleDateFormat("yyyy-MM-dd").parse(text)
                    onStartDateChange(date)
                }
                startDate.value = text
            },
            label = { Text("开始日期") },
            modifier = Modifier.fillMaxWidth().focusable().onFocusChanged {
                if(it.hasFocus) {
                    DatePickerDialog.show{
                        startDate.value = it.name
                        onStartDateChange(it.date())
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
        )

        // 结束日期输入
        OutlinedTextField(
            value = endDate.value?.toString() ?: "",
            onValueChange = { text ->
                if(text.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                    val date = SimpleDateFormat("yyyy-MM-dd").parse(text)
                    onEndDateChange(date)
                }
                endDate.value = text
            },
            label = { Text("结束日期") },
            modifier = Modifier.fillMaxWidth().focusable().onFocusChanged {
                if(it.hasFocus) {
                    DatePickerDialog.show{
                        endDate.value = it.name
                        onEndDateChange(it.date())
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
        )
    }
}
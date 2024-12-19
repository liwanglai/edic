package com.ochess.edict.presentation.main.components

import android.annotation.SuppressLint
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePickerDialog as DatePickerDialog0
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Date

class DatePickerDialog() {
    companion object {
        class DateValue(val time: Long, val name: String, val y: Int, val m: Int, val d: Int) {
            fun date(): Date{
                return Date(time)
            }
        }

        val openDialog = mutableStateOf(false)
        lateinit var onSelectedDo:(dv:DateValue)->Unit
        private fun onSelected(time:Long) {
            val name = SimpleDateFormat("yyyy-MM-dd").format(Date(time?:0))
            val a = name.split("-").map { it.toInt() }
            onSelectedDo(DateValue(time?:0,name,a[0],a[1],a[2]))
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @SuppressLint("UnrememberedMutableState")
        @Composable
        fun add() {
            if (openDialog.value) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
                DatePickerDialog0(
                    onDismissRequest = {
//                        openDialog.value = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                datePickerState.selectedDateMillis?.let { onSelected(it) }
//                            println("选中时间戳为： ${datePickerState.selectedDateMillis}")
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("取消")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }

        fun show(onSelect:(DateValue)->Unit) {
            this.onSelectedDo = onSelect
            openDialog.value = true
        }
    }
}
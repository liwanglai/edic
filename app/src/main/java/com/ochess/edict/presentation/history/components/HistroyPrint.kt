package com.ochess.edict.presentation.history.components

import android.os.Handler
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ochess.edict.bluetooth.Constants
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.print.MPrinter
import com.ochess.edict.view.PopMenu

@Composable
fun HistoryPrint(
    viewModel: HistoryViewModel,
    mPrinter: SnapshotStateMap<String, String>,
    typeIndex: Int,
    dateIndex: Int
) {
    Dialog(
        onDismissRequest = {
            viewModel.showPrintDialog.value = false
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(
                    Modifier.padding(16.dp, 32.dp, 16.dp, 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("打印")
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                    AnimatedVisibility(visible = mPrinter["name"] != null) {
                        val isContextMenuVisible = rememberSaveable {
                            mutableStateOf(false)
                        }
                        val pressOffset = remember {
                            mutableStateOf(DpOffset.Zero)
                        }
                        PopMenu(
                            visible = isContextMenuVisible,
                            offset = pressOffset,
                            items = Constants.bluetoothList
                        ) { k, v ->
                            mPrinter["name"] = k
                            mPrinter["address"] = v
                            MPrinter.printer.upStatus(MPrinter.StatusNames.noConnect)
                            //MPrinter.printer.connect()
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "打印设备：")
                            //Constants.devices
                            Spacer(
                                modifier = Modifier
                                    .width(4.dp)
                            )
                            Text(text = mPrinter["title"] ?: "",
//                                            color = Color.Red,
                                modifier = Modifier
                                    .pointerInput(true) {
                                        detectTapGestures(
                                            onPress = {
                                                MPrinter.printer.initBluetooth()
                                                MPrinter.printer.scanDevice {
                                                    isContextMenuVisible.value = false
                                                    Handler().post {
                                                        isContextMenuVisible.value = true
                                                    }
                                                }
                                                isContextMenuVisible.value = true
                                                pressOffset.value = DpOffset(
                                                    it.x.toDp(),
                                                    it.y.toDp()
                                                )
                                            })
                                    }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "类型:")
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                        )
                        RadioButton(selected = typeIndex == 0, onClick = {
                            viewModel.selectTypeIndex.value = -1
                        })
                        Text(text = "全部", Modifier.padding(0.dp))
                        RadioButton(selected = typeIndex == 1, onClick = {
                            viewModel.selectTypeIndex.value = 1
                        })
                        Text(text = "模糊", Modifier.padding(0.dp))
                        RadioButton(selected = typeIndex == 2, onClick = {
                            viewModel.selectTypeIndex.value = 2
                        })
                        Text(text = "生词", Modifier.padding(0.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "日期:")
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                        )
                        RadioButton(selected = dateIndex == 0, onClick = {
                            viewModel.selectDateIndex.value = 0
                        })
                        Text(text = "今天", Modifier.padding(0.dp))
                        RadioButton(selected = dateIndex == 1, onClick = {
                            viewModel.selectDateIndex.value = 1
                        })
                        Text(text = "本周", Modifier.padding(0.dp))
                        RadioButton(selected = dateIndex == 2, onClick = {
                            viewModel.selectDateIndex.value = 2
                        })
                        Text(text = "本月", Modifier.padding(0.dp))
                    }

                    var selectedOption by remember { mutableStateOf("1") }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "份数:")
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                        )
                        var expanded by remember { mutableStateOf(false) }
                        // 触发下拉的文本框
                        TextField(
                            value = selectedOption,
                            onValueChange = { selectedOption = it },
                            singleLine = true,
                            label = { Text("") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, "dropdown icon", modifier = Modifier.clickable { expanded = true }) // 点击图标展开下拉菜单)
                            }
                        )
                        // 下拉菜单
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // 下拉菜单项
                            val options = (1..50).map { it.toString() }
                            options.forEach { text ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOption = text
                                        expanded = false // 选择一项后关闭下拉菜单
                                    }
                                ) {
                                    Text(text)
                                }
                            }
                        }
                    }
                    var canClick by remember { mutableStateOf(true) }
                    Button(
                        onClick = {
                            canClick = false
                            Log.d("HistoryScreen", "print button Clicked")
                            MPrinter.printer.upStatus(MPrinter.StatusNames.startPrint)
                            viewModel.printHistoryList(selectedOption.toInt())
                            Thread {
                                Thread.sleep(500)
                                canClick = true
                            }.start()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = canClick && MPrinter.printer.canPrint()
                    ) {
                        Text(text = "确定")
                    }
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}
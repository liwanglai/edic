package com.ochess.edict.presentation.history.components

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.home.components.AutoCompleteTextField
import com.ochess.edict.presentation.main.components.CloseButton
import com.ochess.edict.presentation.main.components.DateRangePicker
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.DateUtil
import com.ochess.edict.util.TimeStampScope
import com.ochess.edict.presentation.main.extend.MText as Text
import java.text.SimpleDateFormat
import java.util.Date


class HistroyFilter{
    enum class types {
        key,
        type,
        date,
        level
    }

    companion object{
        val showTypes = arrayListOf(types.type,types.date,types.level)
        private var showDialog by mutableStateOf(false)
        private lateinit var hViewModel:HistoryViewModel
        private var timeRange by mutableStateOf(TimeStampScope(System.currentTimeMillis(),System.currentTimeMillis()))
        var onChange :(type:Int,
                     date:TimeStampScope?,
                     levels: ArrayList<String>,
                     key:String)->Unit = {a,b,c,d-> }
        @Composable
        fun add(vm:HistoryViewModel){
            hViewModel = vm
            if(showDialog) {
//                ActivityRun.onKeyBoardStatusChange {
//                    if(!it){
//                        showDialog = false
//                    }
//                }
                AlertDialog(
                    onDismissRequest = {
//                        showDialog = false
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("过滤")
                            Spacer(Modifier.weight(1f))
                            CloseButton(onClick = {
                                showDialog = false
                            })
                        }
                    },
                    text = {
                        RowList()
                    },
                    confirmButton = { }
                    , modifier = Modifier.alpha(0.9f)
                )
            }
        }

        fun show() {
            showDialog=true
        }

        fun eventChange(onchange:(type:Int,
                        date:TimeStampScope?,
                        levels: ArrayList<String>,
                        key:String) -> Unit = {a,b,c,d-> }
        ) {
            onChange = onchange
        }



        @SuppressLint("UnrememberedMutableState")
        @Composable
        fun RowList(){
            val key = remember { mutableStateOf("") }
            val typeIndex = hViewModel.selectTypeIndex.collectAsState()
            val dateIndex = hViewModel.selectDateIndex.collectAsState()
            val levelIndexs = hViewModel.selectLevels.collectAsState()
            LaunchedEffect(typeIndex.value,dateIndex.value,levelIndexs.value,timeRange){
                val timeStamp:TimeStampScope? = when(dateIndex.value) {
                    1 ->  DateUtil.today()
                    2 ->  DateUtil.thisWeek()
                    3 ->  DateUtil.thisMonth()
                    4 ->  timeRange
                    5 -> {
                        DateUtil.abhsDays()
                    }
                    else -> {
                        null
                    }
                }
                onChange(typeIndex.value, timeStamp,levelIndexs.value,key.value)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .alpha(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(types.key in showTypes) {
                    filterKey(key)
                }
                if(types.type in showTypes) {
                    filterType(typeIndex)
                }
                if(types.date in showTypes) {
                    filterDate(dateIndex)
                }
                if(types.level in showTypes) {
                    filterSearch(levelIndexs)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(levelIndexs.value) { _, it ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = it)
                                Spacer(modifier = Modifier.weight(1f))
                                CloseButton(onClick = {
                                    val index = levelIndexs.value.indexOf(it)
                                    levelIndexs.value.removeAt(index)
                                    val a = arrayListOf<String>()
                                    a.addAll(levelIndexs.value)
                                    a.add(it)
                                    hViewModel.selectLevels.value = a
                                })
                            }
                        }
                    }
                }
            }
        }

        @Composable
        private fun filterKey(key:MutableState<String>) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "关键字:")
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                )
                Text(key.value)
            }
        }
        @Composable
        private fun filterSearch(levelIndexs: State<ArrayList<String>>) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "分类:")
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                )
                val wordViewModel = GlobalVal.wordViewModel
                val keyboardController = LocalSoftwareKeyboardController.current
                var textValue = remember {
                    mutableStateOf(TextFieldValue())
                }
                AutoCompleteTextField(
                    suggestions = wordViewModel.suggestions,
                    onSearch = {
                        wordViewModel.prefixMatcher(it){
                            true
                        }
                    },
                    onClear = {
                        wordViewModel.clearSuggestions()
                    },
                    onDoneActionClick = {
                        keyboardController?.hide()
                    },
                    onItemClick = {
                        val a = arrayListOf<String>()
                        a.addAll(levelIndexs.value)
                        a.add(it)
                        hViewModel.selectLevels.value = a
                        wordViewModel.suggestions.value = emptyList()
//                        textValue.value = TextFieldValue()
                    },
                    itemContent = {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        @SuppressLint("UnrememberedMutableState", "SimpleDateFormat")
        @Composable
        @Preview
        private fun filterDate(dateIndex:State<Int>) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ){
                Text(text = "日期:", modifier = Modifier.padding(0.dp,15.dp,0.dp,0.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = dateIndex.value == 0, onClick = {
                            hViewModel.selectDateIndex.value = 0
                        })
                        Text(text = "不限", Modifier)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = dateIndex.value == 1, onClick = {
                            hViewModel.selectDateIndex.value = 1
                        })
                        Text(text = "今天", Modifier)
                        RadioButton(selected = dateIndex.value == 2, onClick = {
                            hViewModel.selectDateIndex.value = 2
                        })
                        Text(text = "本周", Modifier)
                        RadioButton(selected = dateIndex.value == 3, onClick = {
                            hViewModel.selectDateIndex.value = 3
                        })
                        Text(text = "本月", Modifier)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(selected = dateIndex.value == 5, onClick = {
                            hViewModel.selectDateIndex.value = 5
                        })
                        Text(text = "艾宾浩斯遗忘曲线", Modifier)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = dateIndex.value == 4, onClick = {
                            hViewModel.selectDateIndex.value = 4
                        })
                        Text(text = "自定义", Modifier)

                        if (dateIndex.value == 4) {
                            val startDate = mutableStateOf( SimpleDateFormat("yyyy-MM-dd").format(Date(timeRange.start)))
                            val endDate = mutableStateOf( SimpleDateFormat("yyyy-MM-dd").format(Date(timeRange.end)))
                            DateRangePicker(startDate,endDate,
                                onStartDateChange = {
                                    //timeRange.start = it.time
                                    timeRange = TimeStampScope(it.time, timeRange.end)
                                },
                                onEndDateChange = {
                                    timeRange = TimeStampScope(timeRange.start,it.time)
//                                    timeRange.end = it.time
                                }
                            )
                        }
                    }
                }

            }



        }


        @Composable
        fun filterType(typeIndex:State<Int>) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "类型:",modifier = Modifier.padding(0.dp,15.dp,0.dp,0.dp))
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                )
                Column{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = typeIndex.value == -1, onClick = {
                            hViewModel.selectTypeIndex.value = -1
                        })
                        Text(text = "全部", Modifier)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = typeIndex.value == WordModel.STATUS_KNOW, onClick = {
                            hViewModel.selectTypeIndex.value = WordModel.STATUS_KNOW
                        })
                        Text(text = "认识", Modifier)

                        RadioButton(selected = typeIndex.value == WordModel.STATUS_VAGUE, onClick = {
                            hViewModel.selectTypeIndex.value = WordModel.STATUS_VAGUE
                        })

                        Text(text = "模糊", Modifier)
                        RadioButton(selected = typeIndex.value == WordModel.STATUS_FORGET, onClick = {
                            hViewModel.selectTypeIndex.value = WordModel.STATUS_FORGET
                        })
                        Text(text = "生词", Modifier)
                    }
                }


            }

        }

    }

}

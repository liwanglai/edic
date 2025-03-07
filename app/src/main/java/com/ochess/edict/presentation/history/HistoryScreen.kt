package com.ochess.edict.presentation.history

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochess.edict.R
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.model.TestHistory
import com.ochess.edict.presentation.history.components.HistoryPrint
import com.ochess.edict.presentation.history.components.HistroyFilter
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.WordItemList
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.print.MPrinter
import com.ochess.edict.util.DateUtil
import com.ochess.edict.util.ToastUtil
import com.ochess.edict.view.MPopMenu

var textValue: String = "test"


@OptIn(ExperimentalUnitApi::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(arg: Bundle?, viewModel: HistoryViewModel, onItemClick: (Int) -> Unit) {
    remember {
        val type = arg!!.getInt("type")
        if (type == 0) {
            viewModel.search(type = 0, date = DateUtil.abhsDays())
        } else {
            viewModel.search(type = type)
        }
        type
    }
    var boxVisable by remember{ mutableStateOf(PageConf.getInt(PageConf.HistoryPage.defaultHistoryView)) }
    val history by viewModel.history.collectAsState()
    val showPrint by viewModel.showPrintDialog.collectAsState()

    val selectTypeIndex by viewModel.selectTypeIndex.collectAsState()
    val selectDateIndex by viewModel.selectDateIndex.collectAsState()
    val message by viewModel.toastMessage.collectAsState()

    val typeIndex = selectTypeIndex
    val dateIndex = selectDateIndex
    val toastMessage = message

    //var textValue = "test"
    val mPrinter by  rememberUpdatedState(newValue = MPrinter.printer.status)
    MPrinter.printer.onStatusChange { statusName:String->
        if(statusName == MPrinter.StatusNames.printEnd){
            viewModel.showPrintDialog.value = false
            MPrinter.printer.upStatus(MPrinter.StatusNames.connected)
        }
    }

    val dicType = remember {
        PageConf.getInt(PageConf.homePage.DicType,1)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                val title= remember { mutableStateOf("History") }
                val titleMenu = MPopMenu.HistoryTypes()
                val hView = PageConf.getInt(PageConf.HistoryPage.defaultHistoryView)
                if(hView>0){
                    title.value = titleMenu.items.get(hView).title
                }

                Text(
//                    text = "History",
                    mt(title.value),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.combinedClickable(
                        onClick={
                            titleMenu.show{ _, v->
                                boxVisable = v.value as Int
                                title.value = v.name
                                //testVisable = true
                            }
                        },
                        onLongClick = {
                        }
                    )
                )
                titleMenu.add()
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                if (false) {
                    Text(
//                    text = "Game",
                        text = "游戏",
//                    stringResource(R.string.Game),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 0.dp, top = 0.dp)
                            .clickable {
                                val cb = fun(params: ArrayList<String>): Boolean {
                                    NavScreen.LineGame.open(params.joinToString(","))
//                                    ActivityRun.start(GameActivity::class.java.name, params)
                                    return true
                                }
                                viewModel.getGameList(cb)
//                            history.forEach {
//                                params.add(it.word);
//                            }
//                        ActivityRun.start(GameActivity::class.java.name, params)
                            }
                    )

                    Text(
//                    text = "Print",
                        text = "打印",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            viewModel.showPrintDialog.value = true
                        }
                    )

//                Text(
//                    text = "load",
//                    style = MaterialTheme.typography.h6,
//                    color = MaterialTheme.colors.onBackground,
//                    modifier = Modifier.clickable {
//                        ActivityRun.context.startActivity(Intent(ActivityRun.context,CopyDataActivity::class.java))
//                    }
//                )
//                Text(
////                    text = "Filter",
//                    text = "过滤",
//                    style = MaterialTheme.typography.h6,
//                    color = MaterialTheme.colors.onBackground,
//                    modifier = Modifier
//                        .padding(start = 16.dp, end = 16.dp, bottom = 0.dp, top = 0.dp)
//                        .clickable {
//                            HistroyFilter.show()
//
//                        }
//                )
                    IconButton(onClick = {
                        boxVisable = 1
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.book),
                            contentDescription = "得分",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                IconButton(onClick = { HistroyFilter.show() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "过滤",
                        tint = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.padding(top=-10.dp)
                    )
                }
            }

            when (boxVisable) {
                        0 -> {

                            WordItemList(list = history, onItemClick = onItemClick,dicType=dicType) {
                                viewModel.deleteHistory(it)
                            }
                            HistroyFilter.eventChange{type,date,levels,key->
                                viewModel.search(type=type,date = date,levels=levels,key=key)
                            }
                        }
                        1-> {
                            HistoryBookScreen()
                            HistroyFilter.eventChange{type,date,levels,key->
                                BookHistroy.key.value = key
                                BookHistroy.date.value = date
                            }
                        }
                        2-> {
                            val testModel: TestHistory = viewModel()
                            testModel.select()
                            HistoryTestScreen(testModel.historys)
                            HistroyFilter.eventChange{type,date,levels,key->
                                testModel.select(key,date)
                            }
                        }
            }

            if (showPrint) {
                MPrinter.printer.init()
                HistoryPrint(viewModel, mPrinter, typeIndex, dateIndex)
            }
        }
        if (history.isEmpty()) {
            Text(
                text = mt("empty_history"),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraLight,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }

        if (toastMessage.isEmpty().not()) {
            ToastUtil.showToast(toastMessage)
        }
    }

    HistroyFilter.add(viewModel)
}

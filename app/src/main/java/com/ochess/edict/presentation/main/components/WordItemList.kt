package com.ochess.edict.presentation.main.components

import androidx.collection.arrayMapOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.home.PAGE_FROM_HISTORY
import com.ochess.edict.presentation.main.extend.setTimeout
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.DateUtil
import kotlinx.coroutines.delay


@ExperimentalUnitApi
@Composable
fun WordItemList(
    list: List<WordModel>,
    dicType : Int = 0,
    onItemClick: (Int) -> Unit,
    onDeleteClick: (WordModel) -> Unit
) {
    val titleMap= arrayMapOf<Int,String>()

    GlobalVal.historyGroup.clear()
    var words = arrayListOf<String>()
    var title = ""
    list.forEachIndexed{index,item->
        words.add(item.word)
        val th = DateUtil.formatDateToDaysAgo(item.date.substring(0,13))
        val thTitle = item.date.substring(0, 10) + " " +th
        if (!title.equals(thTitle)) {
            if(title.length>0){
                GlobalVal.historyGroup[title] = words.clone() as List<String>
                words.clear()
            }

            title = thTitle
            titleMap[index] =title
        }
    }
    GlobalVal.historyGroup[title] = words.clone() as List<String>

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        var title =""
        itemsIndexed(list) { index, item ->
            if (titleMap[index]!=null) {
                Spacer(modifier = Modifier.height(15.dp))
                title = titleMap[index]!!
                TimeItem(title)
            }
            WordItem(index, wordModel = item, title, onDeleteClick,dicType=dicType)

        }

    }

}

@Composable
fun TimeItem(lastDate: String) {
    Text(text = lastDate)
}


@ExperimentalUnitApi
@Composable
fun WordItem(
    index: Int,
    wordModel: WordModel,
    title: String,
    onDeleteClick: (WordModel) -> Unit,
    dicType: Int =0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .wrapContentHeight()
            .clickable {
                BookConf.onChaptersChange{}
                BookConf.instance.apply {
                    setContent("历史记录页",GlobalVal.historyGroup)
                    save(title,false)
                    next(wordModel)
                }
                NavScreen.openHome(PAGE_FROM_HISTORY)
            },
        shape = RoundedCornerShape(8.dp),
//        elevation = 0.dp
//        backgroundColor = MaterialTheme.colorScheme.surface,
//        contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(4f)
            ) {
                Row {
                    Text(
                        text = wordModel.word,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        wordModel.date,
                        color = Color.Gray,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                }
                val mean = when(dicType){
                    0-> "${wordModel.meanings?.get(0)?.def}"
                    else -> "${wordModel.meanings?.get(0)?.def_ch}"
                }
                Text(
                    text = "1. ${mean}",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    lineHeight = TextUnit(16f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                val synonyms =  when(dicType){
                    0-> "Synonym(s):"
                    else -> "同义词："
                }
                wordModel.meanings?.get(0)?.synonyms?.let {
                    Text(
                        text = "${synonyms} ${
                            it.toString().removePrefix("[")
                                .removeSuffix("]")
                        }",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = TextUnit(14f, TextUnitType.Sp),
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                    )
                }
                val ex = when(dicType){
                    0-> "Example:${wordModel.meanings?.get(0)?.example}"
                    else -> "例句：${wordModel.meanings?.get(0)?.example_ch}"
                }
                wordModel.meanings?.get(0)?.example?.let {
                    Text(
                        text = ex,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = TextUnit(14f, TextUnitType.Sp),
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            IconButton(onClick = { onDeleteClick(wordModel) }, modifier = Modifier.weight(.5f)) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

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
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
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
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.util.DateUtil


@ExperimentalUnitApi
@Composable
fun WordItemList(
    list: List<WordModel>,
    onItemClick: (Int) -> Unit,
    onDeleteClick: (WordModel) -> Unit
) {
    val titleMap= arrayMapOf<Int,String>()

    GlobalVal.historyGroup.clear()
    var words = arrayListOf<String>()
    var lastDate=""
    var title = ""
    list.forEachIndexed{index,item->
        words.add(item.word)
        if (!lastDate.equals(item.date.substring(0, 10))) {
            if(title.length>0){
                GlobalVal.historyGroup[title] = words.clone() as List<String>
                words.clear()
            }

            lastDate = item.date.substring(0, 10)
            title = lastDate+ "  " +DateUtil.formatDateToDaysAgo(lastDate)
            titleMap[index] =title
        }
    }
    GlobalVal.historyGroup[title] = words.clone() as List<String>

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        itemsIndexed(list) { index, item ->
            if (titleMap[index]!=null) {
                Spacer(modifier = Modifier.height(15.dp))
                TimeItem(titleMap[index]!!)
            }
            WordItem(index, wordModel = item, onItemClick, onDeleteClick)

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
    onItemClick: (Int) -> Unit,
    onDeleteClick: (WordModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .wrapContentHeight()
            .clickable {
                onItemClick(index)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.surface)
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
                        style = MaterialTheme.typography.subtitle1,
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
                Text(
                    text = "1. ${wordModel.meanings?.get(0)?.def}",
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 2,
                    lineHeight = TextUnit(16f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                wordModel.meanings?.get(0)?.synonyms?.let {
                    Text(
                        text = "Synonym(s): ${
                            it.toString().removePrefix("[")
                                .removeSuffix("]")
                        }",
                        style = MaterialTheme.typography.subtitle2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = TextUnit(14f, TextUnitType.Sp),
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                    )
                }
                wordModel.meanings?.get(0)?.example?.let {
                    Text(
                        text = "Ex: $it",
                        style = MaterialTheme.typography.subtitle2,
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

package com.ochess.edict.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.presentation.main.components.Display.mtCnReplace
import kotlinx.coroutines.flow.MutableStateFlow


@ExperimentalUnitApi
@Composable
fun HistoryTestScreen(historys: MutableStateFlow<List<TestEntity>>) {
    val testHistorys = historys.collectAsState()
    Column {
//        Text(
//            text = mt("TestHistory"),
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.onBackground
//        )
        LazyColumn {
            itemsIndexed(testHistorys.value) { index, it ->
                var showMore by remember { mutableStateOf(false) }
                val bColor = if (index % 2 == 0) Color.Gray else MaterialTheme.colorScheme.background
                val name = mtCnReplace(it.name)
                val content = mtCnReplace(it.content)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(bColor)
                    .clickable {
                        showMore = !showMore
                    }
                ) {
                    Text(text = name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = (it.score * 100).toInt().toString(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(it.inTime, Modifier)
                }
                if (showMore) {
                    Row(
                        Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .fillParentMaxWidth()
                    ) {
                        Text(text = content)
                    }
                }
            }
        }
    }
}
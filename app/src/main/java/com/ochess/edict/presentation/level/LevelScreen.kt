package com.ochess.edict.presentation.level

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.local.entity.LevelEntity
import com.ochess.edict.presentation.main.components.Display.mt

@Composable
fun LevelScreen(viewModel: LevelViewModel, onItemClick: (Int, LevelEntity) -> Unit) {

    val levelListState by viewModel.levelList.collectAsState()
    var levelState = levelListState

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = mt("Level"),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
                    .align(Alignment.Start)
            )

            LevelList(list = levelState) { index, entity ->
                onItemClick.invoke(index, entity)
            }
        }

        if (levelState.isEmpty()) {
            Text(
                text = "Level is empty.",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.ExtraLight,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun LevelList(
    list: List<LevelEntity>,
    onItemClick: (Int, LevelEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        itemsIndexed(list) { index, item ->
            LevelItem(index, item, onItemClick)
        }
    }
}
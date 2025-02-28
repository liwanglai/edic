package com.ochess.edict.presentation.listenbook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.model.Article

/**
 * 2025-02-27 规划是做单词点击的后面发现可以在listenbook中实现这个类暂时废弃 无用
 */
@Composable
@Preview
fun BookShowScreen(bid:Int) {
    BookConf.instance;
    //val adata = Article.find(bid)
    val bColor = MaterialTheme.colorScheme.background
    val textToRead = listOf<String>("aa","bb","cc")
    val textColors = arrayListOf<Color>()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize() // Remove padding
                .padding(5.dp)
        ) {
            Box {
                val scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize() // Remove padding

                ) {
                    items(textToRead.size) {
                        textColors.add(bColor)
                        Text(
                            text = textToRead[it],
                            modifier = Modifier
                                .background(textColors[it])
                                .fillMaxWidth()
                                .clickable {

                                }
                        )
                    }
                }
            }
        }
    }
}
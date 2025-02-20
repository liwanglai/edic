package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.presentation.main.extend.bgRun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.ochess.edict.presentation.main.extend.MText as Text

@Composable
fun SaveButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit={}) {
    var sBgColor by remember { mutableStateOf(Color.Gray) }
    sBgColor = MaterialTheme.colorScheme.background
    bgRun{
        CoroutineScope(Dispatchers.IO).launch {
            val viewModel = GlobalVal.wordViewModel;
            if (viewModel.isInBookmark()) {
                sBgColor = Color.Gray;
            }
        }
    }
    Card(
//        elevation = 0.dp,
//        onClick = {
//            onButtonClick()
//        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable {
            onButtonClick()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(sBgColor)
                .padding(8.dp)
                .size(48.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.save),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Save",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
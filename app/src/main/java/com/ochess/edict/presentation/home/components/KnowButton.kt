package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KnowButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    Card(
//        elevation = 0.dp,
//        onClick = {
//            onButtonClick()
//        },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.clickable {
            onButtonClick()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(Color.Green)
                .padding(16.dp,8.dp,16.dp,8.dp)
                .size(48.dp)
        ) {
//            Icon(
//                painterResource(id = R.drawable.save),
//                contentDescription = null,
//                tint = MaterialTheme.colors.onBackground
//            )
            Text(
                text = "认识",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
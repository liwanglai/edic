package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ochess.edict.R

@Composable
fun CopyButton(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {

    Card(
//        elevation = 0.dp,
//        onClick = {
//              onButtonClick()
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
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .size(48.dp)
        ) {
            Icon(painterResource(id = R.drawable.copy), contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground)
            Text(
                text = "Copy",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
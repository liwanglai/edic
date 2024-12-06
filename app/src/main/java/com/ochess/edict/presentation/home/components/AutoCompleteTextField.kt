package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ochess.edict.R
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    modifier: Modifier = Modifier,
    suggestions: MutableStateFlow<List<String>>,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onDoneActionClick: () -> Unit = {},
    onItemClick: (t:String) -> Unit = {},
    itemContent: @Composable (t:String) -> Unit = {}
) {
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var expandedState by remember { mutableStateOf(false) }
    val sug by suggestions.collectAsState()
    LaunchedEffect (sug){
        expandedState=sug.isNotEmpty()
    }
    ExposedDropdownMenuBox(
        expanded = expandedState,
        onExpandedChange = { expandedState = !expandedState },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.text.isNotBlank())
                    onSearch(it.text)
            },
            singleLine = true,
            placeholder = {
                Text(
                    text = "",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search), contentDescription = null
                )
            },
            trailingIcon = {
                if (searchQuery.text.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = TextFieldValue()
                        onClear()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.clear),
                            contentDescription = null
                        )
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
//                backgroundColor = MaterialTheme.colorScheme.surface,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardActions = KeyboardActions(onDone = {
                onDoneActionClick()
            }),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (sug.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expandedState,
                onDismissRequest = { expandedState = false },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surface
                )
            ) {
                sug.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            onItemClick(label)
                            searchQuery = TextFieldValue(
                                label.toString(),
                                selection = TextRange(label.toString().length)
                            )
                            expandedState = false
                    },text={
                            itemContent(label)
                    })
                }
            }
        }
    }
}
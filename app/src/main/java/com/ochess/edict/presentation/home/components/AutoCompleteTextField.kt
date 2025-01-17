package com.ochess.edict.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ochess.edict.R
import com.ochess.edict.presentation.main.extend.MainRun
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    modifier: Modifier = Modifier,
    text:String = "",
    suggestions: MutableStateFlow<List<String>>,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onDoneActionClick: () -> Unit = {},
    onItemClick: (t:String) -> Unit = {},
    autoFocus :Boolean = true,
    itemContent: @Composable (t:String) -> Unit = {}

) {
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(
            annotatedString = AnnotatedString(text=text),
            selection = if(text.length>0) TextRange(0,text.length) else TextRange.Zero
        ))
    }
    var expandedState by remember { mutableStateOf(false) }
    val sug by suggestions.collectAsState()
    val requester = FocusRequester()

    if(autoFocus) {
        MainRun(200) {
            requester.requestFocus()
        }
    }

    expandedState=sug.isNotEmpty()

    Column {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
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
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .focusable()
                .focusRequester(requester)
                .onFocusChanged {
                    if(it.isFocused){
                        onSearch(text)
                    }else{
                        onClear()
                    }
                }
        )
        if (sug.isNotEmpty()) {
            Card(modifier = Modifier
                .padding(5.dp)
                .alpha( 0.8f )
                .verticalScroll(rememberScrollState())
            ) {
                sug.forEach {
                    var word = it
                    var ch = ""
                    if(it.matches(Regex("\\w+:[^:]+"))){
                        val split2 = it.split(":")
                        word = split2[0]
                        ch = split2[1]
                    }
                    Row(modifier = Modifier.clickable {
                        onItemClick(word)
                    }.fillMaxWidth().height(30.dp).padding(start = 10.dp, bottom = 10.dp)){
                        Text(word, modifier = Modifier.weight(1f))
                        if(ch.length>0){
                            Text(ch, color = Color.Gray)
                        }
                    }

                }
            }
//        ExposedDropdownMenuBox(
//            expanded = expandedState,
//            onExpandedChange = {
//                if (it) {
//                    requester.requestFocus()
//                } else {
//                    focusManager.clearFocus()
//                }
//                expandedState = it
//            },
//            modifier = modifier.fillMaxWidth()
//        ) {
//            ExposedDropdownMenu(
//                expanded = expandedState,
//                onDismissRequest = {expandedState=true  },
//                modifier = Modifier.background(
//                    MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
//                )
//            ) {
//                sug.forEach { label ->
//                    DropdownMenuItem(
//                        onClick = {
//                            onItemClick(label)
//                            searchQuery = TextFieldValue(
//                                label.toString(),
//                                selection = TextRange(label.toString().length)
//                            )
//                            expandedState = false
//                    },text={
//                            itemContent(label)
//                    }
//                    )
//                }
//            }
//        }
        }
    }
}
package com.ochess.edict.view

import android.os.Handler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.WordState
import com.ochess.edict.presentation.home.components.AutoCompleteTextField
import kotlinx.coroutines.InternalCoroutinesApi

@Composable
fun SearchButton(onClink:() -> Unit){
    val icon = if (isSystemInDarkTheme()) R.drawable.search else R.drawable.search
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        modifier = Modifier
            .size(20.dp, 20.dp)
            //.offset(5.dp)
            .clickable {onClink() }
        ,
        tint = colorResource(
            id = R.color.text
        )
    )
}
@Composable
fun SearchTool(wordViewModel :WordModelViewModel){
    val keyboardController = LocalSoftwareKeyboardController.current
    var lastSugTime = System.currentTimeMillis()
    val requester = remember {
        FocusRequester()
    }
    var canSug = remember {
        true
    }
    Row (modifier = Modifier
        .padding(15.dp)
        .height(50.dp)
        ,
    ){
        var visible by remember { GlobalVal.isSearchVisible }
        var beforState: WordState? = null

        AnimatedVisibility(visible = visible) {
            AutoCompleteTextField(
                    modifier = Modifier.fillMaxWidth()
                    .focusable().focusOrder(requester).onFocusChanged {
                        if(it.isFocused) {
                            beforState = wordViewModel.wordState.value
                            //wordViewModel.detailState.value = false
                            wordViewModel.wordState.value = WordState(null)
//                            ActivityRun.onKeyBoardStatusChange { isOpen ->
//                                if(visible && !isOpen) visible=false
//                            }
                        }else{
                            //失去焦点还原原始单词
                            if(beforState!=null && wordViewModel.wordState.value.wordModel==null) {
                                visible=false
                                wordViewModel.wordState.value = beforState as WordState
                            }
                        }
                    },
                suggestions = wordViewModel.suggestions,
                onSearch = {
                   if(canSug) {
                       wordViewModel.prefixMatcher(it) {
                           wordViewModel.searcher(it)
                       }
                       canSug = false
                   }else{
                       Handler().postDelayed({
                           if(System.currentTimeMillis() - lastSugTime > 2800) {
                               canSug = true
                           }
                       },3000)
                   }
                    lastSugTime = System.currentTimeMillis()
                },

                onClear = {
                    wordViewModel.clearSuggestions()
                },
                onDoneActionClick = {
                    keyboardController?.hide()
                    visible = false
                },
                onItemClick = {
                    wordViewModel.searcher(it)
                    keyboardController?.hide()
                    visible = false
                },
                itemContent = {
                    Text(
                        text = it,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            )

            Handler().postDelayed({
                requester.requestFocus()
            },200)
        }
    }
}
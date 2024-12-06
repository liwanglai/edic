package com.ochess.edict.presentation.home.homescreen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.WordState
import com.ochess.edict.presentation.home.components.ForgetButton
import com.ochess.edict.presentation.home.components.KnowButton
import com.ochess.edict.presentation.home.components.VagueButton
import com.ochess.edict.presentation.navigation.NavScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FunctionButtons(viewModel: WordModelViewModel, wordState: WordState
                   , onNextWord:(Int)->Unit
) {

    KnowButton(modifier = Modifier
        .combinedClickable (
            onClick = {
                wordState.wordModel?.let {
                    it.status = WordModel.STATUS_KNOW
                    viewModel.insertHistory(it)
                    onNextWord(WordModel.STATUS_KNOW)
                }
            },
            onLongClick = {
                GlobalVal.nav.navigate("${NavScreen.routes.History}?type="+WordModel.STATUS_KNOW)
            }
        )
    ) {}

    VagueButton (modifier = Modifier.combinedClickable (
        onClick = {
            wordState.wordModel?.let {
                it.status = WordModel.STATUS_VAGUE
                viewModel.insertHistory(it)
                onNextWord(WordModel.STATUS_VAGUE)
            }
        },
        onLongClick = {
            GlobalVal.nav.navigate("${NavScreen.routes.History}?type="+WordModel.STATUS_VAGUE)
        }
    )){

    }

    ForgetButton(modifier = Modifier.combinedClickable (
        onClick = {
            wordState.wordModel?.let {
                it.status = WordModel.STATUS_FORGET
                viewModel.insertHistory(it)
                onNextWord(WordModel.STATUS_FORGET)
            }
        },
        onLongClick = {
            GlobalVal.nav.navigate("${NavScreen.routes.History}?type="+WordModel.STATUS_FORGET)
        })){}
}
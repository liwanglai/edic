package com.ochess.edict.presentation.home

import androidx.compose.runtime.Composable
import com.ochess.edict.presentation.home.jdc.JdcFragment
import com.ochess.edict.presentation.home.jdc.new_word
import com.ochess.edict.presentation.home.jdc.recite_word
import com.ochess.edict.presentation.home.jdc.select_book
import com.ochess.edict.presentation.home.jdc.study_word
import com.ochess.edict.presentation.home.jdc.write_word

@Composable
fun JdcScreen(page: Int) {
    when(page)
    {
        -1 ->
            select_book()
        0->
            JdcFragment()
        1->
            study_word()
        2 ->
            recite_word()
        3 ->
            write_word()
        4 ->
            new_word()
    }
}
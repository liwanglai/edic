package com.ochess.edict.presentation.home.jdc

import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.collection.arrayMapOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import com.ochess.edict.R
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.layouts

var book = BookConf.instance
var tIndex = 0
var box: ViewGroup?=null
var rightCount =0
var errorCount =0
var yoursAnswersMap = arrayMapOf<String,String>()
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JdcFragment() {
    book = BookConf.instance
    LayoutJdc.view(layouts.main){
        it.apply {
            val a = arrayOf(
                findViewById<ImageButton>(R.id.ib_study_word),
                findViewById<ImageButton>(R.id.ib_recite_word),
                findViewById<ImageButton>(R.id.ib_dictate_word),
                findViewById<ImageButton>(R.id.ib_new_word)
            )
            a.forEachIndexed { index, imageButton ->
                imageButton.setOnClickListener {
                     NavScreen.openJdc(index+1)
                }
//                val w = 280
//                val p = imageButton.parent as ViewGroup
//                 p.children.forEach {
//                    it.with(w)
//                }
            }

            findViewById<ImageButton>(R.id.back).setOnClickListener {
                LayoutJdc.goHome()
//                NavScreen.openHome(PAGE_FROM_HOME)
            }
            findViewById<TextView>(R.id.ib_set_word).setOnClickListener {
                BookConf.selectBook()
            }
            findViewById<TextView>(R.id.tv_cur_book).apply{
                setOnClickListener {
                    BookConf.selectBook()
                }
                setText("当前课本:"+book.name)
            }
        }
    }
}

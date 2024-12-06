package com.ochess.edict.presentation.home.game

import android.content.Context.WINDOW_SERVICE
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.ochess.edict.MainActivity
import com.ochess.edict.R
import com.ochess.edict.presentation.home.game.ext.LineGame
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ScreenUtil
import kotlin.math.abs

@Composable
fun LineGameScreen(words: List<String>) {
    val act = ActivityRun.context as MainActivity
    var orientation = act.resources.configuration.orientation
    val layoutId = remember { mutableStateOf(0) }
    ActivityRun.onOrientationChange{ orientation->
        layoutId.value=getLayout(orientation);
    }
    layoutId.value=getLayout(orientation)
    AndroidView(factory = {
        //加载AndroidView布局。
        LayoutInflater.from(it).inflate(layoutId.value, null)
    }) { it ->
        val lg = LineGame()
        lg.args.addAll(words)
        lg.onCreate(it)
    }
}



fun getLayout(orientation: Int): Int {
    val act = ActivityRun.context as MainActivity
    var orientation = orientation
    val point = Point()
    (act.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
    //方屏另一个模版
    if (abs((point.x - point.y).toDouble()) < 60) {
        orientation = Configuration.ORIENTATION_UNDEFINED
    }

    return when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> R.layout.activity_wordsearch_game_old
        Configuration.ORIENTATION_LANDSCAPE ->  R.layout.activity_wordsearch_landlayout_game
        Configuration.ORIENTATION_UNDEFINED -> R.layout.activity_wordsearch_game
        else -> R.layout.activity_wordsearch_game_old
    }
}

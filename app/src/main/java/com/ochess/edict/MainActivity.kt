package com.ochess.edict

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ochess.edict.data.UserStatus
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.ui.MTheme
import com.ochess.edict.ui.Main
import com.ochess.edict.ui.bindkeyBoradEvent
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ScreenUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val levelModel: LevelViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityRun.setConText(this)
        if(!UserStatus.get{
                it.getBoolean("showSystemTitle",false)
        }){
            ScreenUtil.setFullScreen(this)
        }
        enableEdgeToEdge()
        levelModel.getAllLevel()
        setContent {
            MTheme  {
                Surface(color = MaterialTheme.colorScheme.background) {
                        Main()
                }
            }
        }
    }

    override fun onDestroy() {
//        val sharedPreferences = this.getSharedPreferences(THEME, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putInt(LEVEL, viewModel.selectLevel.value)
//        editor.apply()
        super.onDestroy()
    }

    fun onOrientationChange(v: View) {
        val b = v as Button
        val orientation = when (b.text.toString()) {
            "横屏" ->  Configuration.ORIENTATION_LANDSCAPE
            "竖屏" ->  Configuration.ORIENTATION_PORTRAIT
            "方屏" ->  Configuration.ORIENTATION_UNDEFINED
            else -> Configuration.ORIENTATION_LANDSCAPE
        }
        ActivityRun.setOrientationChange(orientation)
    }

    override fun onBackPressed(){
        if(ActivityRun.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        bindkeyBoradEvent(window.decorView)
    }
    //获取导航栏的高度
    private val Context?.navigationBarHeight: Int
        get() {
            this ?: return 0
            val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            var height = 0
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId)
            }
            return height
        }

}

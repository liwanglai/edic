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
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.presentation.main.AboutScreen
import com.ochess.edict.presentation.ui.theme.EnglishWhizTheme
import com.ochess.edict.ui.MTheme
import com.ochess.edict.ui.MainContent
import com.ochess.edict.ui.RootContent
import com.ochess.edict.ui.bindkeyBoradEvent
import com.ochess.edict.ui.theme.EdicTheme
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ScreenUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebugActivity : AppCompatActivity() {
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
                    AboutScreen()
                }
            }
        }
    }
}

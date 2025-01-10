@file:OptIn(ExperimentalUnitApi::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)

package com.ochess.edict.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Process
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity.WINDOW_SERVICE
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.viewinterop.AndroidView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.compose.rememberNavController
import com.ochess.edict.R
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.presentation.main.MainScreen
import com.ochess.edict.presentation.main.components.BottomNavigationBar
import com.ochess.edict.presentation.main.components.Confirm
import com.ochess.edict.presentation.main.components.PrivacyPolicy
import com.ochess.edict.util.ActivityRun
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun bindkeyBoradEvent(view:View){
    val run={
        val r = Rect()
        view.rootView.getWindowVisibleDisplayFrame(r)
        //屏幕显示高度
        val outMetrics = DisplayMetrics()
        (view.context
            .getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
            outMetrics
        )
        val heightDiff = outMetrics.heightPixels - (r.bottom - r.top)
        val navigationBarHeight: Int = 50;//view.context.navigationBarHeight
        val isOpen: Boolean = if (heightDiff > navigationBarHeight) { //软键盘弹出
            //软键盘弹出高度 heightDiff
            true
        } else { //
            //软键盘收起
            false
        }
        ActivityRun.onKeyBoardStatusChange(isOpen)
    }
    //延迟500ms之后再去触发键盘事件
    view.rootView.viewTreeObserver.addOnGlobalLayoutListener {
       GlobalScope.launch {
           delay(500)
           run()
       }
    }
    /*
    //获取屏幕高度
    val screenHeight = this.windowManager.defaultDisplay.height
    //阀值设置为屏幕高度的1/3
    val keyHeight = screenHeight/3
    window.decorView.addOnLayoutChangeListener { _, _, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        var isOpen=true
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
            isOpen = true
        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
            isOpen = false
        }
       ActivityRun.onKeyBoardStatusChange(isOpen)
    } */
}

@Composable
fun Main(){
    //Android判断程序是否第一次启动   https://www.jb51.net/article/109558.htm
    val setting: SharedPreferences = ActivityRun.context.getSharedPreferences("toy.keli.edic", 0)
    val user_first = setting.getBoolean("FIRST", true)
    if (user_first) { // 第一次则跳转到欢迎页面
        ActivityRun.onBackPressed { false }
        PrivacyPolicy{
            setting.edit().putBoolean("FIRST", false).commit()
            ActivityRun.restart()
        }
        return
    }

    val mType = MenuConf.type()
    when (mType.value) {
        MenuConf.right ->
            RootContent()
        MenuConf.bottom ->{
            MainContent()
        }
    }
}
@OptIn(InternalCoroutinesApi::class)
@SuppressLint("ClickableViewAccessibility", "InflateParams")
@Composable
@Preview
fun RootContent() {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
     AndroidView(factory = {
            //加载AndroidView布局。
            LayoutInflater.from(it).inflate(R.layout.activity_main, null)
        }) { it ->
            val drawerLayout: DrawerLayout = it.findViewById(R.id.content_drawer)
            val left: View = it.findViewById(R.id.main_left)
            //drawerLayout.openDrawer(Gravity.RIGHT)

            drawerLayout.setDrawerListener(object : DrawerLayout.DrawerListener{
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                    if (drawerView == left) {
                        drawerLayout.closeDrawer(Gravity.LEFT)
                        (ActivityRun.context as Activity).onBackPressed()
                    }
                }

                override fun onDrawerClosed(drawerView: View) {

                }

                override fun onDrawerStateChanged(newState: Int) {
//                        if(newState == )
                }
            })
            val ct: ComposeView = it.findViewById(R.id.conten_parent)
            val mu: ComposeView = it.findViewById(R.id.right_menu)
            ct.setContent {
                MainScreen(
                    navController = navController,
                    scaffoldState = scaffoldState,
                )
            }
            mu.setContent {
                BottomNavigationBar(
                    navController
                ) {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                    drawerLayout.closeDrawers()
                }
            }

    }
}

@Composable
fun MainContent(){
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController
            ) {
                navController.navigate(it.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.padding(
                bottom = it.calculateBottomPadding()
            )
        ) {
            MainScreen(
                navController = navController,
                scaffoldState = scaffoldState,
            )
        }
    }

}
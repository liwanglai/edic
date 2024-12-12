package com.ochess.edict.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Configuration.ORIENTATION_SQUARE
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ochess.edict.MainActivity
import com.ochess.edict.R
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.SettingConf
import com.ochess.edict.print.PrintUtils
import android.os.Process


class ActivityRun {
    companion object {
        lateinit var launcher: ActivityResultLauncher<Intent>
        lateinit var context:Context

        fun setConText(ct:Context) {
            context = ct
            val act = ct as ComponentActivity
            launcher = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(result)
            }

            //配置主题
            val tid = UserStatus().get("dark_theme")
            if(tid>0) {
                SettingConf.changeTheme(tid)
            }
            //配置方向
            val org=UserStatus().getString("Orientation")
            if(org.isNotEmpty())
                ct.setRequestedOrientation(org.toInt())

            //配置语言
            val configuration: Configuration = ct.getResources().getConfiguration()
            val targetLocale = SettingConf.getLanuage()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(targetLocale)
            } else {
                configuration.locale = targetLocale
            }
            ct.resources.updateConfiguration(configuration, ct.resources.displayMetrics)

        }

        fun context(): Context {
            return context
        }
//        fun start(cname: String, vararg params: String) {
        fun start(cname: String, params: ArrayList<String> = arrayListOf()) {
            val ct:Context = context()
            val intent = Intent(ct, Class.forName(cname))
            intent.putExtra("size",params.size)
//            intent.setFlags()
            for(i in params.indices){
                intent.putExtra(i.toString(), params[i])
            }

            ct.startActivity(intent)
        }

        @JvmStatic
        fun params(intent: Intent): Array<String> {
            var rt =  ArrayList<String>()
            val size = intent.getIntExtra("size",0)
            return Array(size) { i -> intent.getStringExtra("$i").toString() }
        }
        fun getSnapshot(): Bitmap? {
            val act = context as Activity
            val view: View = act.window.decorView
//            view.isDrawingCacheEnabled = true
//            view.buildDrawingCache()
//            val bitmap = view.drawingCache
            val bitmap = PrintUtils.getCustomViewBitmap(view.rootView)

            val rectangle = Rect()
            act.window.decorView.getWindowVisibleDisplayFrame(rectangle)
            val top = rectangle.top
            if (bitmap == null) return null
            val bp = Bitmap.createBitmap(bitmap, 0, top, bitmap.width, bitmap.height - top)
            return bp
        }
        //是否横屏
        fun isHorizontalScreens(): Boolean {
            val ori = getScreensOrientation()
            val ct = context as Activity
            val rotation: Int = ct.windowManager.getDefaultDisplay().getRotation()
            if(ori == ORIENTATION_PORTRAIT) return false
            if (ori == ORIENTATION_SQUARE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)) {
                return false;
            }
            return true
        }

        /**
         * ORIENTATION_PORTRAIT：竖屏模式，设备的屏幕方向垂直于地面。
         * ORIENTATION_LANDSCAPE：横屏模式，设备的屏幕方向水平于地面。
         * ORIENTATION_SQUARE：正方形模式，设备的屏幕方向垂直或水平于地面。
         * ORIENTATION_UNDEFINED：未定义的模式，无法确定设备的屏幕方向。
         */
        fun getScreensOrientation(): Int {
            return context.getResources().getConfiguration().orientation
        }

        var event = Events()
        fun onKeyBoardStatusChange(open: Boolean) {
            event.onKeyboardShowHide(open)
        }

        fun onKeyBoardStatusChange(onChange: (isOpen: Boolean) -> Unit) {
            event.onKeyboardShowHide = onChange
        }

        private fun onActivityResult(res: ActivityResult) {
            event.onActivityResult(res)
        }
        fun onActivityResult(reg: (result: ActivityResult) -> Unit) {
            event.onActivityResult = reg
        }

        fun msg(s: String) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        }

        fun runOnUiThread(function: () -> Unit) {
            val act = context as Activity
            act.runOnUiThread(function)
        }

        /**
         * 返回按钮事件拦截
         */
        fun onBackPressed(): Boolean {
            return event.onBackPressed()
        }
        fun onBackPressed(reg: () -> Boolean) {
            event.onBackPressed = reg
        }

        /**
         * 屏幕方向切换事件
         */
        fun onOrientationChange(function: (ori:Int) -> Unit) {
            event.onOrientationChange = function
            val mOrientationEventListener = object : OrientationEventListener(context) {
                override fun onOrientationChanged(orientation: Int) {
                    function(orientation);
                }
            }
            (mOrientationEventListener as OrientationEventListener).enable()
        }

        /**
         * 设置屏幕方向
         */
        fun setOrientationChange(orientation: Int) {
            val ct = context as MainActivity
            when (orientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    ct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

                }
                Configuration.ORIENTATION_UNDEFINED -> {
                    ct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
            }
            event.onOrientationChange(orientation)
        }

        fun restart() {
                // 结束进程
//                Process.killProcess(Process.myPid())
                // 通过系统重新启动应用
                System.exit(0)
        }
    }

    class Events{
        var onOrientationChange: (ori: Int) -> Unit = {}
        var onBackPressed: () -> Boolean = {true}
        var onActivityResult: (result: ActivityResult) -> Unit = fun(res){}
        var onKeyboardShowHide = fun(isOpen:Boolean){}
    }

}
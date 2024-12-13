package com.ochess.edict.presentation.main.components

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.presentation.main.extend.match
import com.ochess.edict.print.PrintUtils
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.util.utilImage

object Display {
    var isHorizontalScreens = mutableStateOf(ActivityRun.isHorizontalScreens())
    @Composable
    fun size(){
        val fullHeight = with(LocalContext. current) {
            resources. displayMetrics. heightPixels
        }

        val fullWith = with(LocalContext. current) {
            resources. displayMetrics. widthPixels
        }

    }
    fun px2dp(width: Int): Dp {
        return PrintUtils.px2dip(ActivityRun.context,width.toFloat()).dp
    }

    fun dp2px(dpValue: Dp): Float {
        val context = ActivityRun.context
        val scale = context.getResources().getDisplayMetrics().density;
        return dpValue.value.toFloat() * scale + 0.5f
    }
    fun dp2px(dpValue: Double): Double {
        val context = ActivityRun.context
        val scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f
    }
    fun getScreenSize(): Point {
        val point = Point()
        val context = ActivityRun.context
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(
            point
        )
        return point
    }

    private fun pageScreenFile(id:String):String{
        val file = PathConf.screen+"$id.jpg"
        return file
    }
    fun getBitMapByPageScreen(id:String = "",wdp:Dp=0.dp,hdp:Dp=0.dp):ImageBitmap{
        val p = getScreenSize()
        var rt = ImageBitmap(p.x,p.y/2)
        val file = pageScreenFile(id)
        val w = dp2px(wdp) .toInt()
        val h = dp2px(hdp).toInt()
        if(FileUtil.exists(file)){
            var img =BitmapFactory.decodeFile(file)
            if(w>0 && w<h && w<img.width){
                img = utilImage.cut(img,w,0,0,rt.width,h*(rt.width/w))
            }
            rt = img.asImageBitmap()
        }

        return rt
    }
    fun setBitMapByPageScreen(id:String = "") {
        val file  = pageScreenFile(id)
        //不要频繁写入间隔10s
        if (FileUtil.exists(file) && FileUtil.mctime(file) > System.currentTimeMillis() - 10000) return
        val bt = ActivityRun.getSnapshot()
        utilImage.bitmap2jpg(bt, file)
    }

    /**
     *
     * 多语言字符串  Multilingual Text
     */
    fun mt(text:String):String{
        val context = ActivityRun.context
        val resources = context.resources
        val identifier: Int = resources.getIdentifier(text, "string", context.getPackageName())

        try {
            //stringResource
            return if (identifier > 0) resources.getString(identifier) else text
        }catch (e:Exception){
            return text
        }
    }

    /**
     * 中文替换多语言
     */
    fun mtCnReplace(text:String): String {
        val chAll = match(text,"[\u4E00-\u9FA5]+",0,true)
        var newText = text
        chAll.forEach {
            newText = newText.replace(it,mt(it))
        }
        return newText
    }
}
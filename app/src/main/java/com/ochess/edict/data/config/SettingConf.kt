package com.ochess.edict.data.config

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.arrayMapOf
import com.ochess.edict.data.UserStatus
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.currentThemeNow
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu
import com.ochess.edict.view.MPopMenu.dataClass
import java.util.Locale

class SettingConf {
    operator fun set(name: String, value: String) {
//        val kClass=this::class.java
//        val field = kClass.getField(name[0].uppercase()+name.substring(1))
//        field.set(this,"")
        when(name){
            "menuMode" ->
                this.MenuMode=""
            "themeType"->
                this.ThemeType=""
            "blueTooth" ->
                this.BlueTooth=""
            "quit"->
                this.Quit=""
            "horizontalDrawAble" ->
                this.HorizontalDrawAble=""
            "orientation"->
                this.Orientation=""
            "language"->
                this.Language=""
            "ret"->
                this.Ret=""
            "systemTitle" ->
                this.SystemTitle = ""
            "about" ->
                this.About = ""
            "privacy" ->
                this.Privacy = ""
            else ->{

            }
        }
    }

    fun toList(): List<Map.Entry<String, String>> {
//        val jsonStr = ObjectMapper().writeValueAsString(this)
//        val map: Map<String, String> = ObjectMapper().readValue(jsonStr, Map::class.java) as Map<String, String>
//        return map.asIterable().toList()

//        val rt = this::class.java.fields.map {
//            object:Map.Entry<String, String>{
//                override val key = it.name
//                override val value = it.get(this).toString()
//            }
//        }
//        if(true) return rt
        val attrs = linkedMapOf(
//            "ret" to this.Ret,
            "language" to this.Language,
            "homePageSetting" to this.HomePageSetting,
            "HistoryPageSetting" to this.HistoryPageSetting,
            "scapesGamePageSetting" to this.ScapesGamePageSetting,
            "blueTooth" to this.BlueTooth,
            "horizontalDrawAble" to this.HorizontalDrawAble,
            "menuMode" to this.MenuMode,
            "themeType" to this.ThemeType,
            "orientation" to this.Orientation,
            "systemTitle" to this.SystemTitle, //显示系统标题栏
            "privacy" to this.Privacy, //显示系统标题栏
            "about" to this.About, //显示系统标题栏
            "quit" to this.Quit,
        )
        return attrs.map{it}
    }

    companion object{
            val sharedPreferences = ActivityRun.context.getSharedPreferences("MainActivity.THEME", Context.MODE_PRIVATE)
//            val mPrinter = MPrinter.printer.status
            val bNameMap = arrayMapOf(false to "close" ,true to "open")
            val organizationMap= linkedMapOf(-2 to "autoChange",ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE to "landscapeScreen",ActivityInfo.SCREEN_ORIENTATION_PORTRAIT to "verticalScreen" )
            val ThemeMap = linkedMapOf(0 to "autoChange",1 to "dayTime" ,2 to "nightTime")
            val options = linkedMapOf(
                    "connectPrint" to MPopMenu.Unspecified,
                    "menuMode" to MPopMenu(MenuConf.nMap.map{
                        dataClass(it.value, value = it.key)
                    }),
                    "language" to MPopMenu(arrayListOf(
                        dataClass("中文",Locale.SIMPLIFIED_CHINESE.displayName,Locale.SIMPLIFIED_CHINESE),
                        dataClass("繁體",Locale.TRADITIONAL_CHINESE.displayName,Locale.TRADITIONAL_CHINESE),
                        dataClass("English", value = Locale.ENGLISH),
//                        dataClass("日本語", value = Locale.JAPANESE),
//                        dataClass("한국어",value = Locale.KOREAN),
//                        dataClass("हिंदीName", value = Locale.US),
//                        dataClass("Deutsch", value = Locale.GERMAN),
//                        dataClass("Français",value = Locale.FRANCE),
//                        dataClass(" Русский язык ", value = Locale.ENGLISH),
//                        dataClass("  بالعربية ", value = Locale.ENGLISH),
//                        dataClass("Español",value=Locale.ENGLISH)
                    )),
                    "orientation"  to MPopMenu(organizationMap.map {
                        dataClass(it.value, value = it.key)
                    }),
                    "themeType" to MPopMenu(ThemeMap.map{
                        dataClass(it.value, value = it.key)
                    })

            )

             fun booleanGet(name:String): String {
                 val config = UserStatus.defInterface
                 val c = config.config
                 return bNameMap[c.getBoolean(name,false)].toString()
             }
             fun booleanSet(name:String){
                var value = false
                UserStatus.get{
                    value = !it.getBoolean(name,value)
                }
                UserStatus.set{
                    it.putBoolean(name,value)
                    NavScreen.Settring.open()
                    return@set name
                }
             }

            fun getLanuage(): Locale {
                val name = UserStatus().getString("Language")
                for (item in options["language"]?.items!!){
                    if(item.title == name)
                    return item.value as Locale
                }

                return Locale.SIMPLIFIED_CHINESE
            }
            /**
             * 主题修改
             */
            fun changeTheme (currentTheme:Int) {
                var color = Color.parseColor("#F4F7FD")
                when (currentTheme){
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    2-> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        color = Color.parseColor("#0C1B3A")
                    }
                }
                Handler().postDelayed({
                    val act = ActivityRun.context as Activity
                    act.window.decorView.setBackgroundColor(color)
                },500)
            }
    }

    public var Privacy: String
        get()= ""
        set(v){
            NavScreen.openRoute(NavScreen.routes.About+"?page=privacy")
        }

    public var About: String
        get()= ""
        set(v){
            NavScreen.openRoute(NavScreen.routes.About)
        }

    public var SystemTitle: String
        get(){
            return booleanGet("showSystemTitle")
        }
        set(v){
            booleanSet("showSystemTitle")
            ActivityRun.restart()
        }

    public val HomePageSetting:String
            get() {
                return "page:"+PageConf.homePage.values().joinToString ( "," )
            }
    public val ScapesGamePageSetting:String
        get() {
            return "page:"+PageConf.sGamePage.values().joinToString ( "," )
        }
    public val HistoryPageSetting:String
        get() {
            return "page:"+PageConf.HistoryPage.values().joinToString ( "," )
        }

        public var MenuMode
            get() = MenuConf.name()
            set(v) {
                val mMenu = options["menuMode"]
                mMenu?.show { k, v ->
                    MenuConf.changeType(v.value as Int)
                }
            }

        public var ThemeType:String
            get() {
                val currentTheme = UserStatus().get("dark_theme")
                var kname = ""
                if(currentTheme ==0) {
                    val tName = mt(ThemeMap[currentThemeNow]!!)
                    kname="($tName)"
                }
                val tName = ThemeMap[currentTheme]
                return mt(tName!!)+kname
            }
            set(v){
                options["themeType"]?.show { k, v ->
                    UserStatus().set("dark_theme", v.value)
                    var tid = v.value as Int
                    if(tid ==0){
                        ActivityRun.restart()
                    }
                    changeTheme(tid)
                    NavScreen.Settring.open()
                }
            }

        public var HorizontalDrawAble:String
            get() = booleanGet("HorizontalDrawAble")
            set(v){
                booleanSet("HorizontalDrawAble")
                ActivityRun.restart()
            }

        public var Orientation:String
            get() {

                return mt(organizationMap[UserStatus().get("Orientation")]!!)
            }
            set(v){
                options["orientation"]?.show { k, v ->
                    val act = ActivityRun.context as Activity
//                    val nowOrg = UserStatus().get("Orientation")
//                    val newOrg =
//                        if (nowOrg == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                        /**竖屏**/
//                        else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE //横屏
                    val newOrg = v.value as Int
                    UserStatus().set("Orientation", newOrg)
                    act.requestedOrientation = newOrg
                    NavScreen.Settring.open()
                }
            }
        public var BlueTooth
            get() = ""
            set(v) {
                val intentBluetooth = Intent()
                intentBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS)
                ActivityRun.context().startActivity(intentBluetooth)
            }
//        var ConnectPrint
//            get() = ""
//            set(v) {
//                MPrinter.printer.initBluetooth()
//                    MPrinter.printer.scanDevice {
//                        val items = arrayListOf<MPopMenu.dataClass>()
//                        it.map {
//                            items.add(MPopMenu.dataClass(it.key, it.key, it.value))
//                        }
//                        pMenu.items = items
//                        pMenu.show { k, v ->
//                            mPrinter["name"] = k
//                            mPrinter["address"] = v.value as String
//                            MPrinter.printer.upStatus(MPrinter.StatusNames.noConnect)
//                        }
//                    }
//            }
        public var Quit
            get()=""
            set(v){
                NavScreen.openRoute(NavScreen.routes.Quit)
            }
        public var Language:String
            get(){
                val local = UserStatus().getString("Language")
                return if(local != "")  local else  "中文"
            }
            set(v){
                options["language"]?.show{ k, v ->
                    UserStatus().set("Language",k)
                    val mContext = ActivityRun.context
                    val targetLocale = v.value as Locale
                    val resources: Resources = mContext.resources
                    val dm = resources.displayMetrics
                    val configuration: Configuration = mContext.resources.configuration
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        configuration.setLocale(targetLocale)
                    } else {
                        configuration.locale = targetLocale
                    }
                    resources.updateConfiguration(configuration, dm) //语言更换生效的代码!
                    NavScreen.openRoute(NavScreen.routes.Setting)
                    NavScreen.refrash()
                }
            }
        public var Ret:String
            get(){
                return ""
            }
            set(v){
                NavScreen.openRoute(NavScreen.routes.Home)
            }

}
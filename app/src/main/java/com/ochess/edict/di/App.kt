package com.ochess.edict.di

import android.app.Application
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.util.ToastUtil
import com.tencent.bugly.crashreport.CrashReport
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (NetConf.tBuglyKey.length>0){
                CrashReport.initCrashReport(applicationContext, NetConf.tBuglyKey, false)
        }
        ToastUtil.initContext(this)
        application = this
    }
    companion object {
       var application:App? = null
    }
}
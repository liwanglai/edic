package com.ochess.edict.di

import android.app.Application
import com.ochess.edict.util.ToastUtil
import com.tencent.bugly.crashreport.CrashReport
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "c3f50abe42", false)
        ToastUtil.initContext(this)
        application = this
    }
    companion object {
       var application:App? = null
    }
}
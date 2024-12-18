package com.ochess.edict.data.config

import com.ochess.edict.util.FileUtil

class PathConf {
    companion object {
        val root = FileUtil.rootDir()+"/"
        val imgs =root+"imgs/"
        val screen = root+"screen/"
        val print = root+"print/"
        val dcim = FileUtil.dcimDir()+"edic/"
    }
}
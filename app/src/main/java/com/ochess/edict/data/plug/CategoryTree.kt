package com.ochess.edict.data.plug

import android.content.res.AssetManager
import com.ochess.edict.data.model.Article
import com.ochess.edict.util.ActivityRun

object CategoryTree {
    const val root = "category_tree"
    private val assetManager: AssetManager = ActivityRun.context.resources.assets
    fun rsyncDataPromPathAssets() {
        val files = getFiles(root)
        for (f in files) {
            val ins = assetManager.open(f)
            val bytes = ByteArray(ins.available())
            ins.read(bytes)
            val data = String(bytes)
            Article.addFile(data,f.substring(root.length+1))
        }
    }
    private fun getFiles(path:String =root): ArrayList<String> {
        val rt = arrayListOf<String>()
        val childs = assetManager.list(path)
        if(childs!=null && childs.size>0) {
            for (f in childs) {
                val subpath = path + "/" + f
                val childPath = getFiles(subpath)
                rt.addAll(childPath)
            }
        }else{
            rt.add(path)
        }

        return rt
    }
}
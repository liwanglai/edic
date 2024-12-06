package com.ochess.edict.view.skin.view

import android.view.View
import android.widget.PopupMenu
import com.ochess.edict.util.ActivityRun

object SkinView {
    @JvmStatic
    fun popMenu(v: View, items:ArrayList<String>, f:(s:String) -> Unit)
    {
        //定义PopupMenu对象
        val pm = PopupMenu(ActivityRun.context, v)
        val m = pm.menu
        items.forEachIndexed{i,item->
            m.add(0, i, 0, item)
        }
        //设置PopupMenu的点击事件
        pm.setOnMenuItemClickListener { item ->
            f(item.toString())
            true
        }
        pm.show()
    }


}
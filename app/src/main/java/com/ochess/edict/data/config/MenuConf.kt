package com.ochess.edict.data.config

import androidx.collection.arrayMapOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.ochess.edict.data.UserStatus
import com.ochess.edict.util.ActivityRun

class MenuConf {
    enum class mode{
        title_word_shows,
        wordStudy,
        jdc_main,
        jdc_select,
        jdc_write,
        //title_game,
        wordScapesGame,
        wordExtGame,

        title_book_shows,
        chapterPage,
        listenBook,   //单词统计单独一行
       // showBook,     //书本原始内容
        findGame,
//        editBook,
//        print,
    }

    companion object {
        const val default=0
        const val right=2
        const val bottom=3
        var config:MutableState<Int>?=null
        val nMap = arrayMapOf(
            default to "autoChange",
            right to "rightHide",
            bottom to "bottomShow"
        )
        @Composable
        fun type(): MutableState<Int> {
            if(config==null) {
                config = UserStatus().getMutableStatus(name = "menu")
            }
            tryUpDefConfig()

            return config!!
        }

        fun tryUpDefConfig(){
            val cf = UserStatus().get("menu")
            if(cf == default) {
                val isHs = ActivityRun.isHorizontalScreens()
                config!!.value = if (isHs) {
                    right
                } else {
                    bottom
                }
            }
        }

        fun changeType(type:Int=0) {
            when(config!!.value){
                default ->
                    config!!.value = default
                right ->
                    config!!.value = bottom
                bottom ->
                    config!!.value = right
            }
            UserStatus().set("menu",config!!.value)
        }

        fun name():String{
            return nMap[UserStatus.defInterface.get("menu")]?:""
        }

        fun modeNow(): mode {
            var index = UserStatus.get{
                it.getInt("viewMode",0)
            }

            val m = mode.values().filter {!it.name.startsWith("title_")  }.get(index)
            return m
        }

        fun modeGroups(): LinkedHashMap<String, ArrayList<mode>> {
            val list = MenuConf.mode.values()
            val itemGroups = linkedMapOf<String,ArrayList<MenuConf.mode>>()
            var group=""


            list.forEach {item->
                if(item.name.startsWith("title_")){
                    group = item.name.substring(6)
                    return@forEach
                }
                if(!itemGroups.containsKey(group)) {
                    itemGroups.set(group, arrayListOf<MenuConf.mode>())
                }
                itemGroups.get(group)?.add(item)
            }

            return itemGroups
        }
    }

}
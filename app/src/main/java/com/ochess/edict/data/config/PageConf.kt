package com.ochess.edict.data.config

import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.SettingConf.Companion
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.view.MPopMenu

class PageConf {
    enum class homePage {
        TitleClickEdit,         //标题单击可编辑
        NextUnordered,          //单词排序乱序
        DefaultShowDetails,     //默认展示详情
//        UpDownDraggable,        //上下可拖动;
        DicType,                 //词典类型
        RemainViewMode,          //下一个单词保持当前的视图模式
        viewMode
    }


    enum class sGamePage {
        AutoNext,           //自动下一个
        LetterLen,          //单词长度过滤
    }

    enum class DicType {
        en_en,     //英英
        en_cn,     //英汉
        encn       //英汉双语
    }
    companion object {
        val options = linkedMapOf(
            "LetterLen" to listOf(0,2,3,4,5,6,7,8,9,10,11).map { it.toString() },
            "DicType" to DicType.values().map { it.name },
            "viewMode" to MPopMenu.ViewModeMenu()
        )

//        public var ViewMode
//            get() = MenuConf.modeNow().name
//            set(v)  {
//                val mMenu = SettingConf.options["viewMode"]
//                mMenu?.show { k, v ->
//                    val vv = (v.value as MenuConf.mode).ordinal
//                    viewMode = MenuConf.modeNow(vv)
//                    NavScreen.openHome(0)
//                }
//            }

        fun getBoolean(t:homePage,defV:Boolean=false): Boolean {
            return UserStatus.get{
                return@get it.getBoolean(t.name,defV)
            }
        }
        fun getBoolean(t:sGamePage): Boolean {
            return UserStatus.get{
                return@get it.getBoolean(t.name,false)
            }
        }
        fun getInt(t:sGamePage): Int {
            return UserStatus.get{
                return@get it.getInt(t.name,0)
            }
        }
        fun getInt(t:homePage,defv:Int=0): Int {
            return UserStatus.get{
                return@get it.getInt(t.name,defv)
            }
        }


    }
}

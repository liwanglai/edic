package com.ochess.edict.data.config

import com.ochess.edict.data.UserStatus

class PageConf {
    enum class homePage {
        TitleClickEdit,         //标题单击可编辑
        NextUnordered,          //单词排序乱序
        DefaultShowDetails,     //默认展示详情
//        UpDownDraggable,        //上下可拖动;
        DicType,                 //词典类型
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
            "DicType" to DicType.values().map { it.name }
        )

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
        fun getInt(t:homePage): Int {
            return UserStatus.get{
                return@get it.getInt(t.name,0)
            }
        }


    }
}

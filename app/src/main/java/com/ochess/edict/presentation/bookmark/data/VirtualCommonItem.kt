package com.ochess.edict.presentation.bookmark.data

import com.ochess.edict.R

/**
 * 虚拟的公共项
 */
class VirtualCommonItem(
    val id:Int,
    val name: String,
    val type:Type,
    val ico:Int = R.drawable.category
) {
    enum class Type{
        level,
        online,
        donwload,
        saved
    }

    companion object {
        fun get(pid: Int): VirtualCommonItem? {
            all.forEach{
                if(it.id == pid) return it
            }
            return null
        }

        fun getId(t:Type): Int {
            all.forEach{
                if(it.type == t) return it.id
            }
            return 0
        }

        val all: List<VirtualCommonItem> = listOf(
            VirtualCommonItem(-4,"Downloads",Type.donwload),
            VirtualCommonItem(-3,"Level",Type.level),
            VirtualCommonItem(-2,"OnLine",Type.online),
            VirtualCommonItem(-1,"Stored",Type.saved,ico=R.drawable.article),
        )
    }

}
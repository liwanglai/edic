package com.ochess.edict.presentation.main.extend

import java.util.regex.Pattern

fun match(str:String,reg: String, flag: Int, mAll: Boolean) :List<String>{
    val m = arrayListOf<ArrayList<String>>()
    val mm = arrayListOf<String>()
    val r = Pattern.compile(reg, flag)
    val rm = r.matcher(str)
    while (rm.find()) {
        if (rm.groupCount() > 0) {
            for (i in 0..rm.groupCount()) {
                val v = rm.group(i)
                if (m.size < i + 1) {
                    m.add(arrayListOf(v))
                } else {
                    m.get(i).add(v)
                }
            }
        } else {
            mm.add(rm.group())
        }
        if (!mAll) break
    }
    if (!mAll) {
        val mm= if (rm.groupCount() > 0) m.get(0) else m
        return mm as List<String>
    }
    if(m.size == 0) return mm
    return m[0] as List<String>
}
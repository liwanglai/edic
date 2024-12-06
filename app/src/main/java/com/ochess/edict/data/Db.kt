package com.ochess.edict.data

import androidx.collection.arrayMapOf
import androidx.room.Room
import com.ochess.edict.util.ActivityRun

class Db{
    companion object{
        private var INSTANCE = arrayMapOf<String,Any>()
        val dictionary:DictionaryDatabase = getDatabase("dictionaryDb")
        @JvmField
        val user:WordModelDatabase = getDatabase("wordModelDb")
        private fun <T> getDatabase(name:String): T {
            if(INSTANCE[name]!=null) {
                return INSTANCE[name] as T
            }
            // 如果 INSTANCE 为 null，则创建数据库实例
            val context = ActivityRun.context
            val instance = Room.databaseBuilder(
                context.applicationContext,
                if(name == "wordModelDb") WordModelDatabase::class.java else DictionaryDatabase::class.java,
                name
            ).build()
            INSTANCE[name] = instance

            return INSTANCE[name] as T
        }
    }
}
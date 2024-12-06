package com.ochess.edict.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ochess.edict.data.local.DictionaryDao
import com.ochess.edict.data.local.LevelDao
import com.ochess.edict.data.local.converters.LabelConverter
import com.ochess.edict.data.local.converters.MeaningConverter
import com.ochess.edict.data.local.converters.SynonymConverter
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.LevelEntity
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent


@TypeConverters(value = [MeaningConverter::class, SynonymConverter::class, LabelConverter::class])
@Database(
    entities = [DictionaryEntity::class, LevelEntity::class],
    exportSchema = true,
    version = 2
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract val dictionaryDao: DictionaryDao
    abstract val levelDao: LevelDao


   // abstract val chineseWordDao:ChineseWordDao
   companion object {
       // 延迟初始化，确保数据库只被创建一次
       @Volatile
       private var INSTANCE: DictionaryDatabase? = null

       @JvmStatic
       fun getDatabase(context: Context): DictionaryDatabase {
           // 如果 INSTANCE 为 null，则创建数据库实例
           return INSTANCE ?: synchronized(this) {
               val instance = Room.databaseBuilder(
                   context.applicationContext,
                   DictionaryDatabase::class.java,
                   "dictionaryDb"
               ).build()
               INSTANCE = instance
               // 返回 instance
               instance
           }
       }

   }


    open fun executeSQL(sql: String?, bindArgs: Array<Any?>?) {
        val db = INSTANCE!!.openHelper.writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(sql!!, bindArgs!!)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // handle exceptions
        } finally {
            db.endTransaction()
        }
    }

    fun select(sql: String): ArrayList<HashMap<String, String>> {
        var rs = this.query(sql,null)
        var rt = ArrayList<HashMap<String,String>>()
        if(!rs.moveToFirst()) return rt
        do {
            var row = HashMap<String,String>()
            for(i in 0 until rs.columnCount) {
                val key: String = rs.getColumnName(i)
                var value: String = ""
                if(!rs.isNull(i)) value = rs.getString(i)
                row.put(key,value)
            }
            rt.add(row)
        }while(rs.moveToNext())
        return rt;
    }

    fun one(sql:String) : java.util.HashMap<String, String>? {
        val rows = select(sql)
        if(rows.size==0) return null
        return select(sql).get(0);
    }

    fun ch(word:String) :String{
        var sql = String.format("select ch from %s_table where word='%s' limit 1" ,word.substring(0,1),word)
        var ch = one(sql)?.get("ch")
        return ch?:""
    }
}
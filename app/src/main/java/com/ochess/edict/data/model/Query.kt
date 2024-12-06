package com.ochess.edict.data.model

import android.database.Cursor
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import java.util.regex.Matcher
import java.util.regex.Pattern

class Query {
    private var args: ArrayMap<String, Any> = arrayMapOf<String,Any>()
    private var tableName: String? = null
    private var select = "*"
    private var where = "1=1"
    private var group: String? = null
    private val haveing: String? = null
    private var order: String? = null
    private var limit: String? = null
    private var cursor: Cursor? = null
    private lateinit var db: RoomDatabase


    constructor(db:RoomDatabase,tabname: String?) {
        this.db = db
        tableName = tabname
    }

    fun setTableName(tableName: String?) {
        this.tableName = tableName
    }

    constructor() {}

    fun warp(`val`: Any): String {
        val v = `val`.toString()
        if (v.indexOf("'") != -1) v.replace("'", "\\'")
        return if (v.matches(Regex("^\\d"))) v else "'$v'"
    }

    fun where(where: String): Query {
        this.where = where
        return this
    }

    fun andWhere(where: String): Query {
        this.where += " and $where"
        return this
    }

    fun orWhere(where: String): Query {
        this.where += " or $where"
        return this
    }

    fun where(key: String?, option: String?, `val`: Any?): Query {
        where += String.format(" and %s %s %s", key, option, `val`)
        return this
    }

    fun where(key: String?, `val`: Any): Query {
        where += String.format(" and %s=%s", key, warp(`val`))
        return this
    }

    fun like(key: String?, `val`: String?): Query {
        where += String.format(" and %s like '%%%s%%'", key, `val`)
        return this
    }

    fun whereIn(key: String?, ida: List<Any>): Query {
        var v: String = ida.joinToString(",")
        if (!v.matches(Regex("^[\\d,\\.]+$"))) {
            if (v.matches(Regex("\\'"))) v = v.replace("\\'".toRegex(), "\\\\'")
            v = "'" + v.replace(",".toRegex(), "','") + "'"
        }
        where += String.format(" and %s in (%s)", key, v)
        return this
    }

    fun where(where: String?, params:ArrayMap<String,Any>): Query {
        this.where = where!!
        this.args = params
        return this
    }

    fun limit(size: Int, page: Int): Query {
        var page = page
        limit = size.toString()
        if (page > 0) {
            page = page * size
            limit = "$page,$size"
        }
        return this
    }

    fun limit(n: Int): Query {
        limit = n.toString()
        return this
    }

    fun order(o: String?): Query {
        order = o
        return this
    }

    fun select(s: String): Query {
        select = s
        return this
    }

    fun groupBy(g: String?): Query {
        group = g
        return this
    }

    fun query(): String {
        val sqlBuilder = StringBuilder()
        sqlBuilder.append("select ").append(select).append(" from ")
            .append(tableName).append(" where ").append(where)
        if (group != null) sqlBuilder.append(" group by $group")
        if (order != null) sqlBuilder.append(" order by $order")
        if (limit != null) sqlBuilder.append(" limit $limit")
        return  sqlBuilder.toString()
    }

    fun build(): SupportSQLiteQuery {

        var sql = query()
        var binds = arrayListOf<Any>()
        val r = Pattern.compile("/:\\w+/")
        val rm: Matcher = r.matcher(sql)
        val a = arrayListOf<String>()
        while (rm.find()) {
            val key = rm.group()
            val value = args[key.substring(1, key.length - 1)]
            a.add(key)
            sql.replace(key, "?")
            binds.add(value!!)
        }

        val queryObj = SimpleSQLiteQuery(sql, binds.toArray())
        return queryObj
    }
     fun <T>all(dao: CanCommandSelectDao) {
//         val rt = MutableStateFlow(arrayOf<>())
//         val queryObj = build()
//         Thread {
//             rt.value = dao.select(queryObj)
//         }.run()
//        return rt
    }

    fun whereBetween(k: String, s: Any, e: Any): Query {
        this.where +=String.format(" and `%s`>'%s' and `%s`<='%s'", k, s,k,e)
        return this
    }

    fun whereBetweens(k: String, dates: ArrayList<Long>): Query {
        val whereOrs = arrayListOf<String>()
        for(i in 0..dates.size-1 step 2){
            val s=dates[i]
            val e=dates[i+1]
            val one = String.format("(`%s`>'%s' and `%s`<='%s')", k, s,k,e)
            whereOrs.add(one)
        }
        this.where +=" and ("+whereOrs.joinToString ( " or " ) + ")"
        return this
    }
    fun andWhere(key: String, id: Any) :Query{
            andWhere("${key}="+warp(id))
        return this
    }

    fun count(): Query {
        this.select = "count(*) as count"
        return this
    }
//
//    fun <T>one(): T {
//        limit(1)
//        var row:T = all()[0]
//        return row
//    }
//
//    fun count(): Int {
//        val tmpSelect = select
//        select = arrayOf("count(*) as count")
//        val rt = Integer.valueOf(getField("count"))
//        select = tmpSelect
//        return rt
//    }
//
//    fun getField(name: String?): String {
//
//    }
//
//    fun delete(): String {
//        val sql = String.format("DELETE from %s where %s", tableName, where)
//        return sql
//    }

}

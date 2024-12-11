package com.ochess.edict.util

import android.os.Build
import android.text.format.DateUtils
import com.ochess.edict.presentation.main.components.Display.mt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


object DateUtil {

    @JvmStatic
    fun today(): TimeStampScope {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return TimeStampScope(calendar.timeInMillis, calendar.timeInMillis+86400*1000)
    }

    @JvmStatic
    fun thisWeek(): TimeStampScope {
        val start = thisWeekStart()
        val end = thisWeekEnd()
        return TimeStampScope(start.time, end.time)
    }

    @JvmStatic
    fun thisWeekStart(): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        return calendar.time
    }

    @JvmStatic
    fun thisWeekEnd(): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        }
        return calendar.time
    }

    @JvmStatic
    fun thisMonthStart(): Date{
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
        }
        return calendar.time
    }

    @JvmStatic
    fun thisMonth(): TimeStampScope{
        return TimeStampScope(thisMonthStart().time, System.currentTimeMillis())
    }

    fun date(t:Long):String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(t))
    }

    fun abhsDays(): TimeStampScope {
        //艾宾浩斯记忆遗忘周期
        val days = listOf(2,7,15)
        val t = today()
        val ts = t.start
        val te = t.end
        var rt = arrayListOf<TimeStampScope>()
        for(d in days){
            val start = ts-d*24*3600*1000
            val name=date(start+1).substring(0,9)
           rt.add(TimeStampScope(start,start+24*3600*1000))
        }
        val stimes= days.map { ts-it*24*3600*1000 }
        return TimeStampScope(stimes[0],stimes.last()+86400*1000,rt)
    }

    fun formatDateToDaysAgo(date: Long): String {
        return formatDateToDaysAgo(Date(date))
    }
    fun formatDateToDaysAgo(date: Date): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val now = System.currentTimeMillis()
            val diff = now - date.time
            if (diff < DateUtils.MINUTE_IN_MILLIS) {
                return mt("刚刚")
            } else if (diff < DateUtils.HOUR_IN_MILLIS) {
                val minutes = (diff / DateUtils.MINUTE_IN_MILLIS).toInt()
                return minutes.toString() + mt("分钟前")
            } else if (diff < DateUtils.DAY_IN_MILLIS) {
                val hours = (diff / DateUtils.HOUR_IN_MILLIS).toInt()
                return hours.toString() + mt("小时前")
            } else {
                val days = (diff / DateUtils.DAY_IN_MILLIS).toInt()
                return days.toString() + mt("天前")
            }
        } else {
            // 对于旧版本的 Android，可以使用旧的方法或者考虑引入第三方库来处理日期格式化
            return "不支持旧版本 Android 的格式化"
        }
    }

    fun formatDateToDaysAgo(date: String,format:String="yyyy-MM-dd"): String {
        return formatDateToDaysAgo( SimpleDateFormat(format).parse(date))
    }

    fun dayRemainderMs(): Long {
        val ts = today()
        return ts.end-System.currentTimeMillis()
    }
}

data class TimeStampScope(var start: Long, var end: Long,val lists:List<TimeStampScope> = listOf())
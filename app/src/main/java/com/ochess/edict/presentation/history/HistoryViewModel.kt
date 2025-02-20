package com.ochess.edict.presentation.history

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import androidx.collection.arraySetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochess.edict.R
import com.ochess.edict.data.Db
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.data.model.Category
import com.ochess.edict.data.model.Query
import com.ochess.edict.data.model.Word
import com.ochess.edict.data.repository.WordRepository
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.print.MPrinter
import com.ochess.edict.print.PrintUtils
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.DateUtil
import com.ochess.edict.util.TimeStampScope
import com.ochess.edict.view.WordSearchView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@SuppressLint("MissingPermission")
@HiltViewModel
class HistoryViewModel @Inject constructor(private val wordRepo: WordRepository): ViewModel() {
    private var selectDate= MutableStateFlow (TimeStampScope(0,0))
    val selectLevels= MutableStateFlow(arrayListOf<String>())
    var history = MutableStateFlow(listOf<WordModel>())
        private set

    var showPrintDialog = MutableStateFlow(false)

    val selectTypeIndex = MutableStateFlow(-1)
    val selectDateIndex = MutableStateFlow(0)
   // var container: LinearLayout = PrintUtils.createContainerView(ActivityRun.context)

    var toastMessage = MutableStateFlow("")
    private var metric = DisplayMetrics() //获取屏幕



    private var width = 0 // 屏幕宽度（像素）

    var height = 0 // 屏幕高度（像素）
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val dateNow = Date(System.currentTimeMillis())
    //今天需要复习的爱丁浩斯的数量
    val edinhosSize = MutableStateFlow(0)
    //是否可以爱丁浩斯 （没有运行过可以运行）
    val canEdinhos = MutableStateFlow(false)

    fun deleteHistory(wordModel: WordModel) {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepo.deleteHistory(wordModel.toHistoryEntity())
        }
    }
    private fun itemToBitmap(printList: List<WordModel>, printDate: String): Bitmap {
        val name = ""
        val date: String = dateFormatter.format(dateNow)

        //container.removeAllViews()
        val container = PrintUtils.createContainerView(ActivityRun.context)
        val headView = PrintUtils.createHeadView(ActivityRun.context)
        val game = WordSearchView(ActivityRun.context,null)
        headView.findViewById<TextView>(R.id.head_name).text = "姓名：$name"
        headView.findViewById<TextView>(R.id.head_date).text = date
        container.addView(headView)
        if(printList.isNotEmpty()) {
            for (item in printList) {
                val itemView = PrintUtils.createWordItem(ActivityRun.context, item.word, item.ch)
                itemView.findViewById<TextView>(R.id.item_word).text = item.word
                itemView.findViewById<TextView>(R.id.item_ch).text = item.ch
                container.addView(itemView)
                game.addWord(item.word)
            }
            game.show(true)
            game.setPadding(5,10,5,50)
            container.addView(game)
        }

        (ActivityRun.context as Activity).windowManager.defaultDisplay.getMetrics(metric) //是获取到Activity的实际屏幕信息。
//      width = (1080/1.5).toInt() //metric.widthPixels // 屏幕宽度（像素）
        width = (411f*metric.density+0.5f).toInt()
        height = metric.heightPixels // 屏幕高度（像素）
        PrintUtils.layoutView(container,width,height)
        val bitmap: Bitmap = PrintUtils.getCustomViewBitmap(container)

        return bitmap
    }
    @SuppressLint("SimpleDateFormat")
    fun printHistoryList(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateIndex = selectDateIndex.value
            val typeIndex = selectTypeIndex.value
            var printDate = ""
            val printList = arrayListOf<HistoryEntity>()

            when (typeIndex) {
                TYPE_ALL -> {
                    when (dateIndex) {
                        DATE_TODAY -> {
                            wordRepo.getHistoryToday().firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = dateFormatter.format(dateNow)
                        }
                        DATE_THIS_WEEK -> {
                            wordRepo.getHistoryThisWeek().firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = "${dateFormatter.format(DateUtil.thisWeekStart())} 至 ${dateFormatter.format(DateUtil.thisWeekEnd())}"
                        }
                        DATE_THIS_MONTH -> {
                            wordRepo.getHistoryThisMonth().firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = "${dateFormatter.format(DateUtil.thisMonthStart())} 至 ${dateFormatter.format(dateNow)}"
                        }
                    }
                }
                TYPE_VAGUE, TYPE_FORGET-> {
                    when (dateIndex) {
                        DATE_TODAY -> {
                            wordRepo.getHistoryToday(typeIndex).firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = dateFormatter.format(dateNow)
                        }
                        DATE_THIS_WEEK -> {
                            wordRepo.getHistoryThisWeek(typeIndex).firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = "${dateFormatter.format(DateUtil.thisWeekStart())} 至 ${dateFormatter.format(DateUtil.thisWeekEnd())}"
                        }
                        DATE_THIS_MONTH -> {
                            wordRepo.getHistoryThisMonth(typeIndex).firstOrNull()?.let {
                                printList.addAll(it)
                            }
                            printDate = "${dateFormatter.format(DateUtil.thisMonthStart())} 至 ${dateFormatter.format(dateNow)}"
                        }
                    }
                }
            }
            //printText(printList)
            for(n in 1..count)
            printListByWordModels(printList.map { it.toWordModel() }, printDate)
        }
    }
    fun printListByWordModels(printList:List<WordModel>, printDate:String=""){
        //printText(printList)
        val printBitmap: Bitmap = itemToBitmap(printList, printDate)
        MPrinter.printer.printImg(printBitmap)

        Log.d(TAG, "printHistoryList() list = $printList")
    }

    fun getGameList(callback: (ArrayList<String>) ->Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameList = ArrayList<String>()
            wordRepo.getHistoryToday(TYPE_FORGET).firstOrNull()?.let {
                for (v in it) {
                    gameList.add(v.word)
                }
                callback(gameList)
            }
        }

    }

    fun search(type: Int, date: TimeStampScope?=null, levels: ArrayList<String>?=null,key:String="",callback:(q:Query)->Unit={query->
        history.value = Db.user.wordModelDao.getHistoryList(query.build()).map{
            it.toWordModel()
        }
    }) {
        val query = Query(Db.user,"historyTable").order("time desc")
        if(type != -1) {
            query.where("status", type)
        }
        if(key!=null && key.length>0){
            query.like("word",key);
        }
        if(date!=null) {
            if(date.lists.size>0){
                var dates = arrayListOf<Long>()
                for(date in date.lists){
                    dates.addAll(listOf( date.start,date.end))
                }
                query.whereBetweens("time",dates)
            }else
                query.whereBetween("time",date.start,date.end)
            selectDate.value = date
        }
        viewModelScope.launch(Dispatchers.IO) {
            callback(query)
            if(levels!=null && levels.size>0) {
                history.value = filterByKeys(levels,history.value)
            }
        }
    }




    init {
        viewModelScope.launch(Dispatchers.IO) {
            canEdinhos.value = UserStatus.get(UserSettingKey_CanEdinhos){
                getBoolean(it,true)
            }

            search(type = 0, date = DateUtil.abhsDays(), callback = {
                edinhosSize.value = Db.user.wordModelDao.count(it.count().build())
            })

            wordRepo.getAllHistory().collect { result ->
                history.value = result.map { it.toWordModel() }.reversed()
            }
        }

        self = this
    }

    companion object{
        lateinit var self:HistoryViewModel
        val UserSettingKey_CanEdinhos= "canEdinhos"
        @SuppressLint("SuspiciousIndentation")
        fun searchDay(day: String) {
            val query = query()
            val date = SimpleDateFormat("yyyy-MM-dd").parse(day)
                query.whereBetween("time",date.time,date.time+86400000)
            self.history.value = Db.user.wordModelDao.getHistoryList(query.build()).map { it.toWordModel() }
        }

        fun printWordModelsToImg(printList:List<WordModel>, file:String=""){
            val printBitmap: Bitmap = self.itemToBitmap(printList, "")
            MPrinter.printer.printImg(printBitmap,file)
        }
        fun query(): Query {
            val query = Query(Db.user,"historyTable").order("time desc")
            return query
        }
        fun selectByType(type: Int, words: List<String>): List<String> {
            val q = query()
            return when(type){
                0 -> {
                    q.whereIn("word",words)
                    val haves =Db.user.wordModelDao.getHistoryList(q.build()).map{it.word}
                    return words.filter{ !(it in haves) }
                }
                else -> {
                    q.where("status",type)
                    return Db.user.wordModelDao.getHistoryList(q.build()).map{it.word}
                }
            }
        }
        fun filterByKeys(inputs: List<String>, query: List<WordModel>): List<WordModel> {
            var rt = query.map{it.word}.toTypedArray()

            runBlocking {
                inputs.asFlow().onEach {
                    val newList = filterByKey(it,query)
                    rt = rt.intersect(newList).toTypedArray()
                }.collect{it}
            }
            return query.filter { rt.contains(it.word)}
        }
        fun filterByKey(input: String, query: List<WordModel>): List<String> {
                val v = input.split(Regex("[:\\.]+"))
                val key = v.first()
                val id = v[1].toInt()
                val value = v.last()

                val rt= when (key) {
//                    "history" -> {
//                        //query.andWhere("word",id)
//                    }

                    "level" -> {
                        query.filter { it.level == id }
//                    query.andWhere("level",id)
                    }

                    "article" -> {
                        val aEntity = Db.user.article.find(id)
                        val haveWords = aEntity.findWords()
                        query.filter { it.word in haveWords }
                        //query.whereIn("word",aEntity.findWords())
                    }

                    "category" -> {
                        val cids = Category.getAllCids(id)
                        val words = arrayListOf<String>()
                        Db.user.article.search().map {
                            words.addAll(it.findWords())
                        }
                        query.filter { it.word in words }
                        //query.whereIn("word",words)
                    }
                    else -> query
                }
           return rt.map { it.word }
        }
        const val TAG = "HistoryViewModel"

        const val TYPE_ALL = 0
        const val TYPE_VAGUE = 1
        const val TYPE_FORGET = 2

        const val DATE_TODAY = 0
        const val DATE_THIS_WEEK = 1
        const val DATE_THIS_MONTH = 2
    }
}
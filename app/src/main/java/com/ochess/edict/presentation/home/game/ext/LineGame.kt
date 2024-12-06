package com.ochess.edict.presentation.home.game.ext

import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.ochess.edict.R
import com.ochess.edict.data.DictionaryDatabase
import com.ochess.edict.data.adapter.StringsAdapter
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FontManager
import com.ochess.edict.util.ScreenUtil
import com.ochess.edict.view.WordSearchView
import java.util.Locale
import java.util.Objects
import kotlin.math.abs


class LineGame  {
    val args = arrayListOf<String>()
    private var ttsListener: TTSListener? = null
    private var gameWords: ListView? = null
    var wordMap: MutableMap<String, String> = HashMap()
    var act = ActivityRun.context as Activity
    fun onCreate(box:View) {
//        super.onCreate(savedInstanceState)
//        args = ActivityRun.params(intent)
        val db: DictionaryDatabase = DictionaryDatabase.getDatabase(act)
        for (arg in args) {
            var zh: String = db.ch(arg)
            zh = zh.replace("^\\w+\\.".toRegex(), "")
            wordMap[arg] = zh
        }
        ttsListener = TTSListener(act) { null }
        initView(box)
        //onOrientationChange(null) //ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    fun initView(view: View?) {
        if(view!=null)
        view.apply {
            val tvStrikethrough = findViewById<TextView>(R.id.tv_strikethrough)
            val backBtn = findViewById<Button>(R.id.tv_back)
            backBtn?.setOnClickListener { v: View? -> finish() }
            tvStrikethrough.text = words
            tvStrikethrough.paintFlags = tvStrikethrough.paintFlags // | Paint.STRIKE_THRU_TEXT_FLAG
            tvStrikethrough.visibility = View.GONE

            val wordsGrid: WordSearchView = findViewById(R.id.wordsGrid)
            wordsGrid.setTypeface(FontManager.getTypeface(act, FontManager.POYNTER))

            for (arg in args) {
                wordsGrid.addWord(arg)
            }

            wordsGrid.show()
            wordsGrid.setOnWordSearchedListener { word ->
                var word = word
                word = wordsGrid.getWord(word).toLowerCase()
                //word=word.toLowerCase();
                for (i in args.indices) {
                    if (args[i] == word) {
                        gameWords!!.getChildAt(i).visibility = View.GONE
                        args[i] = ""
                    }
                }
                tvStrikethrough.text = words
                Toast.makeText(
                    act,
                    word.toString() + " found",
                    Toast.LENGTH_SHORT
                ).show()
                ttsListener?.setLocale(Locale.ENGLISH)
                ttsListener?.speak(word)
                val finalWord: String = word
                Handler().postDelayed({
                    ttsListener?.setLocale(Locale.TAIWAN)
                    ttsListener?.speak(Objects.requireNonNull(wordMap[finalWord]).toString())
                }, 2000)
                if (words.isEmpty()) {
                    Handler()
                        .postDelayed({ onOver() }, 2000)
                }
                wordsGrid.changeDrawColor()
            }


            initWordsListView(tvStrikethrough.parent as ViewGroup, wordsGrid)
        }
    }

    private fun finish() {
        NavScreen.openHome(0)
    }

    fun initWordsListView(box: ViewGroup, wordsGrid: WordSearchView) {
        gameWords = ListView(act)
        val ws = arrayOfNulls<String>(args.size)
        var i = 0
        for (k in args) {
            ws[i++] = k + " " + wordMap[k]
        }
        val gameAdapter: ListAdapter = object : StringsAdapter(ws, true) {
            override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
                val tv = super.getView(i, view, viewGroup) as TextView
                tv.setTextColor(act.resources.getColor(R.color.white))
                return tv
            }
        }
        gameWords!!.adapter = gameAdapter
        gameWords!!.post { setListViewAutoHeight(gameWords) }
        //项目的单击事件
        gameWords!!.onItemClickListener =
            OnItemClickListener { av: AdapterView<*>?, v: View?, index: Int, nuse: Long ->
                val arg = args[index]
                wordsGrid.show(arg)
            }
        box.addView(gameWords)
    }

    /** 计算高度 https://www.cnblogs.com/Westfalen/p/5427402.html  */
    private fun setListViewAutoHeight(lv: ListView?) {
        if (lv == null || lv.count <= 0) return
        var totalHeight = 0
        for (i in 0 until lv.count) {
            val view = lv.adapter.getView(i, null, lv)
            view.measure(0, 0)
            totalHeight += view.measuredHeight
        }
        val params = lv.layoutParams
        params.height =
            totalHeight + lv.dividerHeight * (lv.count - 1) + lv.paddingTop + lv.paddingBottom
        lv.layoutParams = params
    }

    private fun onOver() {
        Toast.makeText(act, "You win!!!", Toast.LENGTH_SHORT).show()
        finish()
    }

    val words: String
        get() {
            val words = StringBuilder()
            for (arg in args) {
                if (!arg.isEmpty()) {
                    words.append(arg).append("\n")
                }
            }
            return words.toString()
        }

}

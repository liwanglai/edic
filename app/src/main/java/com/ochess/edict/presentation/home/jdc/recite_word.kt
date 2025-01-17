package com.ochess.edict.presentation.home.jdc

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.collection.arraySetOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.Glide
import com.ochess.edict.R
import com.ochess.edict.data.Db
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.data.model.Book
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.layouts
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.hide
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.hides
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.id
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.show
import com.ochess.edict.view.skin.view.SkinView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Random


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun recite_word(){
    LayoutJdc.view(layouts.select){
        HomeEvents.onDownMenuShow{
            it.findViewById<TextView>(R.id.s_types).performClick()
//            show(R.id.title)
        }
        HomeEvents.onDownMenuHide{
//            hide(R.id.title)
            it.findViewById<TextView>(R.id.s_types).performClick()
        }
        box = it
        it.apply {
            hides(R.id.ib_back,R.id.tv_title,R.id.title,R.id.tv_question_count,R.id.tv_question_count2,R.id.oknum)
            //返回按钮
            findViewById<ImageButton>(R.id.ib_back).setOnClickListener {
//                NavScreen.openHome(PAGE_FROM_HOME)
                LayoutJdc.goHome()
            }

            //配置标题
            findViewById<TextView>(R.id.tv_title).setText(book.name)

            ////配置章节
            findViewById<TextView>(R.id.tv_unit).setText(BookConf.instance.chapterName)
            //章节选择
            findViewById<View>(R.id.ll_unit).setOnClickListener {
                SkinView.popMenu(it, BookConf.chapters, {
                    book.save(chapterName = it)
                })
            }
            //两个进度条直留一个
            findViewById<TextView>(R.id.tv_question_count2).visibility = View.GONE
            val stbs = findViewById<View>(R.id.stypes)
            findViewById<TextView>(R.id.s_types).setOnClickListener {
                stbs.visibility = if(stbs.visibility == View.GONE)  View.VISIBLE else View.GONE
            }
            //类型按钮
            var buttonTabs = listOf(R.id.btn_recite_1,R.id.btn_recite_2,R.id.btn_recite_3,R.id.btn_recite_4)
            buttonTabs.forEachIndexed{index,id->
                findViewById<Button>(id).apply {
                    isSelected= if(index==0) true else false
                    setOnClickListener {
                        val b = it as Button
                        buttonTabs.forEach { id<Button>(it).isSelected = false }
                        b.isSelected = true
                        tIndex = index
                        rightCount = 0
                        errorCount = 0
                        typetab()
                    }
                }
            }
            findViewById<Button>(buttonTabs[3]).text = mt("看图选词")
            typetab()
        }
    }
}

fun typetab(){
    val ro = Random().nextInt(3)
    var options = listOf(R.id.option_1,R.id.option_2,R.id.option_3,R.id.option_4)
    val oris = listOf(LinearLayout.VERTICAL,LinearLayout.VERTICAL,LinearLayout.VERTICAL,LinearLayout.HORIZONTAL)
    val r = Random()
    val nowWord = listOf(book.word,book.ch,mt("请根据正确的发音选择正确的词"),book.ch)
    if(book.size==0) return

    box?.apply {
        // 进度 tv_question_count
        findViewById<TextView>(R.id.tv_question_count).text = "${book.index+1}/${book.size}"
        //正确和失败的数量
        findViewById<TextView>(R.id.tv_correct).text = rightCount.toString()
        findViewById<TextView>(R.id.tv_wrong).text = errorCount.toString()
        //当前单词 tv_zh
        findViewById<TextView>(R.id.tv_word_name).text = nowWord[tIndex]
        findViewById<View>(R.id.ib_word_speak).setOnClickListener{
            GlobalVal.tts.play(book.word)
        }
        //第四个按钮改为看图选词
        if(tIndex==3) {
            Book.pic {
                Glide.with(context)
                    .load(it)
                    .into(findViewById<ImageView>(R.id.iv_question))
                findViewById<ImageView>(R.id.iv_question).visibility = View.VISIBLE
            }
            findViewById<TextView>(R.id.tv_word_name).visibility = View.GONE
        }else{
            findViewById<ImageView>(R.id.iv_question).visibility = View.GONE
            findViewById<TextView>(R.id.tv_word_name).visibility = View.VISIBLE
        }
        //单选按钮的方向
        findViewById<RadioGroup>(R.id.radioGroup).orientation = oris[tIndex]
        val okOption = findViewById<RadioButton>(options[ro])
        Log.d( "typetab: ","\nword:${book.word} ${book.ch} ok:${ro}")
        val setIndexs = arraySetOf<Int>(book.index)
        options.forEachIndexed { i, id ->
            var index = if(i==ro) book.index else r.nextInt(book.size-1)
            var tryMax=10
            if(i!=ro) {
                //其他选项去重
                while (index in setIndexs && tryMax-- > 0) {
                    index = r.nextInt(book.size-1)
                    if (book.size < 5) break
                }
            }
            setIndexs.add(index)
            val nowWordModel = BookConf.words[index]
            val ch = arrayListOf<String>(nowWordModel.ch!!, nowWordModel.word, nowWordModel.word,nowWordModel.word)
            Log.d( "typetab: ","i:${i} index:${index}.${ch[tIndex]}")
            findViewById<RadioButton>(id).apply {
                text = ch[tIndex]
                isChecked = false
                val rb = this
                isEnabled=true
                setOnClickListener {
                    it.isEnabled=false
                    val ok = (ro==i)
                    LayoutJdc.playStatus(ok)
                    if(ok) {
                        rightCount++
                        rb.setButtonDrawable(R.drawable.correct)
                    }else{
                        errorCount++
                        rb.setButtonDrawable(R.drawable.wrong)
                        okOption.setButtonDrawable(R.drawable.correct)
                    }
                    Handler().postDelayed({
                        if(book.next()) {
                            options.forEach {
                                id<RadioButton>(it).setButtonDrawable(R.drawable.icon_mnkc_select)
                            }
                            typetab()
                        }else{
                            options.forEach {
                                id<RadioButton>(it).setButtonDrawable(R.drawable.icon_mnkc_select)
                            }
                            resultBox(rightCount, errorCount)
                        }
                    },2000)
                }
            }
        }

        //发音
        if(listOf(0,2,3).indexOf(tIndex)!=-1) {
            Handler().postDelayed({
                GlobalVal.tts.play(book.word)
            },500)
        }
    }
}

fun resultBox(rc: Int, ec: Int) {
    val ct= ActivityRun.context
    val pW = LayoutInflater.from(ct).inflate(R.layout.dialog_result, null)
    val dialog = Dialog(ct)
    val starIds = listOf(R.id.start_1,R.id.start_2,R.id.start_3)
    val tsy = ct.resources.getStringArray(R.array.tsy)
    //星级
    val xn = rc*100/(rc+ec)+1
    pW.apply {
        //正确数
        findViewById<TextView>(R.id.tv_result_correct).text = rc.toString()
        //错误数
        findViewById<TextView>(R.id.tv_result_wrong).text = ec.toString()

        starIds.forEachIndexed{index,id->
            val rid = if(xn/33>index) R.drawable.star_checked else R.drawable.star_bg
            findViewById<ImageView>(id).setBackgroundResource(rid)
        }
        //描述语
        findViewById<TextView>(R.id.tv_result).text = tsy[Math.max(0,xn/10-1)]
        //单词学习
        findViewById<Button>(R.id.btn_study_word).setOnClickListener {
            NavScreen.openJdc(1)
            dialog.hide()
        }
        //重新挑战
        findViewById<Button>(R.id.btn_restart).setOnClickListener {
            book.index =0
            rightCount=0
            errorCount=0
            book.next(0)
            dialog.hide()
            typetab()
        }
    }

    dialog.setContentView(pW)
    dialog.show()

    var buttonTabs = listOf(R.id.btn_recite_1,R.id.btn_recite_2,R.id.btn_recite_3,R.id.btn_recite_4)
    val typeName = id<Button>(buttonTabs[tIndex]).text
    val t = System.currentTimeMillis()
    val d = SimpleDateFormat("yyyy-MM-dd HH:mm").format(t)
    GlobalScope.launch {
        Db.user.test.add(
            TestEntity(
                "正确数：$rc 错误数：$ec",
                "选词测试-$typeName-$d",
                BookConf.instance.id,
                xn / 100f,
                t
            )
        )
    }
}
package com.ochess.edict.presentation.home.jdc

import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.collection.arrayMapOf
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.view.children
import androidx.core.view.setPadding
import com.ochess.edict.R
import com.ochess.edict.data.Db
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.BMode
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.BType
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bEditViews
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bInput
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bMode
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bPlay
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bSleeps
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bTimes
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bType
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.bUpper
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.inputStyleLine
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.inputType
import com.ochess.edict.presentation.home.jdc.write_word.ListenWritePlay.Companion.play
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.layouts
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.bv
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.click
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.clicks
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.hide
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.hides
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.id
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.layout
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.radio
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.row
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.show
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.shows
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.text
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


@Composable
fun write_word(){

    //配置显示
    var configShow=false
    val bgColor = MaterialTheme.colors.background.value.toInt()
    LayoutJdc.view(layouts.listen){
        book.onNextDone{
            ActivityRun.runOnUiThread {
                initWord()
            }
        }
        book.initArticle()
        HomeEvents.onDownMenuShow{
//            show(R.id.page1_title)
            id(R.id.tv_config).performClick()
        }
        HomeEvents.onDownMenuHide{
//            hide(R.id.page1_title)
            id(R.id.tv_config).performClick()
        }
        Log.d("write_word: ","刷新视图")
        it.apply {
            id(R.id.page_1).setBackgroundColor(bgColor)
            //隐藏第三页显示第二页,听写间隔 ,
            hides(R.id.ib_back1,R.id.page_0,R.id.page_2,R.id.ll_machine_interval_sequence,R.id.ll_text_interval_sequence,R.id.page1_tv_title,R.id.page1_title)
            //返回
            click(R.id.ib_back1){
                NavScreen.HistoryScreen.open()
//                LayoutJdc.goHome()
            }
            //标题
            text(R.id.page1_tv_title,book.name)
            //报英文,/报中文
            val spackTypeButtons = listOf(R.id.b_english,R.id.b_chinese)
            radio(spackTypeButtons,spackTypeButtons[0])
            clicks(spackTypeButtons){
                bType = BType.values()[it]
                radio(spackTypeButtons,spackTypeButtons[it])
                play()
            }
            //配置按钮
            click(R.id.tv_config) {
                configShow = !configShow
                show(R.id.ll_text_interval_sequence,configShow)
                show(R.id.ll_machine_interval_sequence,configShow)
            }
            //重复次数
            click(R.id.interval){
                bTimes++
                if(bTimes>5) bTimes=1
                text(it.id, bTimes.toString()+mt("次"))
            }
            //大小写
            click(R.id.mode){
                bUpper = !bUpper
                val map = arrayMapOf(true to "大写",false to "小写")
                text(it.id, map[bUpper]!!)
                initWord()
            }
            //间隔时间
            click(R.id.interval_machine){
                bSleeps++
                if(bSleeps>5) bSleeps=1
                text(it.id, bSleeps.toString()+"S")
            }
            //顺序
            click(R.id.mode_machine){
                val v = BMode.values()
                var i = v.indexOf(bMode)+1
                if(i >= v.size){
                    i=0
                }
                bMode = v[i]
                text(it.id, bMode.name)
                ListenWritePlay.upOrder()
            }
            //配置保存
            click(R.id.config_save){
                ActivityRun.msg("待开发")
            }
            //输入方式
            click(R.id.input_type){
                inputType = !inputType
                val map = arrayMapOf(false to "打乱字母",true to "舒尔特方格")
                text(it.id, map[inputType]!!)
                if(inputType) {
                    hide(R.id.line_your_input)
                    show(R.id.line_your_input_smt)
                    hides(R.id.ll_controller,R.id.progressbar)
                }else{
                    hide(R.id.line_your_input_smt)
                    show(R.id.line_your_input)
                    shows(R.id.ll_controller,R.id.progressbar)
                }
                initWord()
            }

            hide(R.id.line_your_input)
            show(R.id.line_your_input_smt)
            hides(R.id.ll_controller,R.id.progressbar)

            //input显示方式
            click(R.id.in_mode){
                inputStyleLine = !inputStyleLine
                val map = arrayMapOf(true to "横线",false to "方框")
                text(it.id, map[inputStyleLine]!!)
                initWord()
            }
            //播放
            click(R.id.ib_play) {
                bPlay=!bPlay
                val ico = arrayMapOf(false to R.drawable.ib_play,true to R.drawable.pause)[bPlay]
                id(R.id.ib_play).setBackgroundResource(ico!!)
                if(bPlay) play()
            }
            //左右键
            clicks(R.id.ib_right,R.id.ib_left) {
                book.next(it==0)
                initWord()
            }
            //进度条
//            id<SeekBar>(R.id.progressbar).apply {
            id<ProgressBar>(R.id.progressbar).apply {
                max=book.size
                setProgress(book.index)
//                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                           book.index = p1
//                           book.next()
//                    }
//                    override fun onStartTrackingTouch(p0: SeekBar?) {}
//                    override fun onStopTrackingTouch(p0: SeekBar?) {}
//                })
            }

            //清除输入
            click(R.id.iv_result_reset){
                bInput=""
                upbInput()
            }
            //界面刷新导致下一个
            if(book.index==0) {
                book.next(0)
            }else{
                initWord()
            }
        }
    }
}


fun upbInput(){
    bEditViews.forEachIndexed{index,ev->
        val c= if(index< bInput.length) bInput.get(index) else ""
        if(!ev.text.toString().equals(c)){
            ev.text = Editable.Factory.getInstance().newEditable(c.toString())
        }
    }
    show(R.id.iv_result_reset, bInput.length>0)
}
fun initWord(){
    bInput = ""
    //输入框
    row(R.id.letter_line).apply {
        if(this==null) return
        removeAllViews()
        //清空单词输入框
        bEditViews.clear()
        val a = 0..book.word.length-1
        a.forEachIndexed {index,it->
            val lid = if(!inputStyleLine) R.layout.letter_edit else R.layout.letter_edit_tingxie
            addView(layout(lid).apply {
                setPadding(5)
                findViewById<EditText>(R.id.edit_letter).apply {
                    bEditViews.add(this)
                    isFocusable = false
                    if(index<bInput.length) {
                        text = Editable.Factory.getInstance().newEditable(bInput[index].toString())
                    }
                    setOnClickListener {
                        bInput = bInput.substring(0,index)
                        upbInput()
                    }
                    setOnLongClickListener {
                        inputStyleLine = !inputStyleLine
                        initWord()
                        true
                    }
                }
            })
        }
    }
    //选择项
    row(R.id.line_your_input).apply {
        //退格键
        val bKey = children.last()
        removeView(bKey)
        removeAllViews()
        book.getInputKeys(bUpper).forEach { c->
            addView(bv<TextView>(R.layout.input_select_keyboard, R.id.s_letter).apply {
                text = c.toString()
                setOnClickListener {
                    bInput+=c
                    upbInput()
                    if(bInput.length==book.word.length){
                        yoursAnswersMap.put(book.word,bInput)
                        onOver(bInput.toUpperCase().equals(book.word.toUpperCase()))
                    }
                    if(bPlay){
                        bPlay=false
                        Handler().postDelayed({ bPlay=true },5000)
                    }
                }
            })
        }
        //添加退格键
        addView(bKey.apply {
            setOnClickListener {
                if(bInput.length>0) {
                    bInput = bInput.substring(0, bInput.length - 1)
                }
                upbInput()
            }
            this.visibility = View.GONE
        })
    }
    row(R.id.line_your_input_smt).apply {
        removeAllViews()
        var rowBox: ViewGroup? =null
        book.getInputKeys(bUpper,25).forEachIndexed{ i,c->
            if(i%5==0){
                rowBox=bv<LinearLayout>(R.layout.input_select_keyboard_smt,R.id.smt_row)
                addView(rowBox)
                rowBox?.removeAllViews()
            }
            rowBox?.addView(bv<TextView>(R.layout.input_select_keyboard, R.id.s_letter).apply {
                text = c.toString()
                setOnClickListener {
                    bInput+=c
                    upbInput()
                    if(bInput.length==book.word.length){
                        yoursAnswersMap.put(book.word,bInput)
                        onOver(bInput.toUpperCase().equals(book.word.toUpperCase()))
                    }
                    if(bPlay){
                        bPlay=false
                        Handler().postDelayed({ bPlay=true },5000)
                    }
                }
            })
        }
    }

    //播放按钮
    if(book.index>0) {
        show(R.id.ib_left)
    }else{
        hide(R.id.ib_left)
    }
    if(book.index == book.size-1) {
        hide(R.id.ib_right)
    }

    //进度
    text(R.id.tv_index_number_fill,"${book.index+1}/${book.size}")
    id<ProgressBar>(R.id.progressbar).setProgress(book.index)
    //按钮隐藏 错误 正确 正确答案
    hides(R.id.iv_result_wrong,R.id.iv_result_correct,R.id.correct_text,R.id.letter_result,R.id.iv_result_reset)
    //没有连续阅读页发个音
    Thread {
        play()
    }.start()
}
fun onOver(ok:Boolean){
    LayoutJdc.playStatus(ok)
    if(ok) {
        hide(R.id.iv_result_wrong)
        show(R.id.iv_result_correct)
        rightCount++
    }else{
        show(R.id.iv_result_wrong)
        hide(R.id.iv_result_correct)
        errorCount++
    }
    text(R.id.letter_result, book.word)
    shows(R.id.letter_result,R.id.correct_text)

    Handler().postDelayed({
        val hasNext = book.next()
        if(!hasNext) {
            book.index=0
            showResult()
        }
    },1000)
}
fun showResult(){
    var bNum =0
    show(R.id.page_2)
    //返回按钮
    click(R.id.ib_back2){
        LayoutJdc.goHome()
    }
    //总数
    text(R.id.tv_result,book.size.toString())
    //正确的数失败的数
    text(R.id.tv_result_correct,rightCount.toString())
    text(R.id.tv_result_wrong,errorCount.toString())
    //全选按钮
    hides(R.id.iv_select_all,R.id.ll_select_all)
    //重新听写
    click(R.id.b_redictation){
        hide(R.id.page_2)
        book.next(0)
    }
    text(R.id.tv_already_add,"已添加"+bNum)
    val everyOne = arrayListOf<String>()
    id<ListView>(R.id.list_view_result).adapter = BookConf.getChapterWords(R.layout.listview_result_item){it,wm->
        it.apply {
            findViewById<TextView>(R.id.number).text = ""
            findViewById<TextView>(R.id.tv_correct_answer).text = wm.word
            findViewById<CheckBox>(R.id.checkbox).visibility = View.GONE
            val bookButton =findViewById<ImageButton>(R.id.tv_add_book)
            bookButton.setOnClickListener{
                GlobalVal.wordViewModel.insertBookmark(wm)
                bNum++
                text(R.id.tv_already_add,"已添加"+bNum)
                bookButton.setBackgroundResource(R.drawable.addhanzied)
            }
            val youAnswer = yoursAnswersMap.get(wm.word)?:""
            val yat =findViewById<TextView>(R.id.tv_yours_answer)
            yat.text = youAnswer
            if(!youAnswer.equals(wm.word) && !youAnswer.toLowerCase().equals(wm.word)){
                yat.setTextColor(R.color.color_wrong_text.toInt())
            }

            everyOne.add("${wm.word}:$youAnswer")
        }
    }

    val t = System.currentTimeMillis()
    val d = SimpleDateFormat("yyyy-MM-dd HH:mm").format(t)
    val f = rightCount.toFloat()/book.size
    GlobalScope.launch {
        Db.user.test.add(
            TestEntity(
                "总数：${book.size} 正确数：$rightCount 错误数：$errorCount\n\n" + everyOne.joinToString(
                    "\n"
                ), "听写测试-$d",
                BookConf.instance.id, f, t
            )
        )
        rightCount = 0
        errorCount = 0
    }
}
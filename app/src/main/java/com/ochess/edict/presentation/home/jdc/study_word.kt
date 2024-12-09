package com.ochess.edict.presentation.home.jdc

import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.children
import com.google.accompanist.glide.rememberGlidePainter
import com.ochess.edict.R
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.model.Word
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.jdc.study_word.tabs
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.InfoDialog
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ActivityRun.Companion.msg
import com.ochess.edict.view.skin.LayoutJdc
import com.ochess.edict.view.skin.LayoutJdc.layouts
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.click
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.img
import com.ochess.edict.view.skin.LayoutJdc.layouts.methods.toggle
import com.ochess.edict.view.skin.view.SkinView
import java.util.Locale

@Composable
fun study_word(){
    book = BookConf.instance
    if(book.name.isEmpty()) {
        NavScreen.openJdc(0)
        return
    }
    book.initArticle()
    LayoutJdc.view(layouts.home) { it ->
        val box = it
        book.onNextDone {
            Handler().postDelayed({
                StudyWord(box, book.wordMode)
            },10)
        }
        //val statusVal = upstatus.value
        it.apply {
            //返回按钮
            findViewById<ImageButton>(R.id.back).setOnClickListener {
                viewMode = MenuConf.mode.jdc_select
                LayoutJdc.goHome()
            }
            //配置标题
            findViewById<TextView>(R.id.tv_title).text = book.name
            ////配置章节
            val chapter = findViewById<TextView>(R.id.tv_unit)
                chapter.text = book.chapterName
            //章节选择
            findViewById<View>(R.id.ll_unit).setOnClickListener {
                SkinView.popMenu(findViewById<TextView>(R.id.tv_title), BookConf.chapters) {
                    book.save(chapterName = it)
                    chapter.text = book.chapterName
                }
            }
            var lastWord:View? = null
            //左侧单词列表
            findViewById<ListView>(R.id.list_view_study).apply{
                adapter = BookConf.getChapterWords(R.layout.item_listview_word) { item, vItem ->
                    item.apply {
                        findViewById<TextView>(R.id.tv_word).text = vItem.word
                        setOnClickListener {
                            lastWord?.setBackgroundResource(0)
                            lastWord = this
                            setBackgroundResource(R.color.design_default_color_secondary)
                            book.next(vItem)
                        }
                        if (lastWord == null) {
                            lastWord = this
                            performClick()
//                            toggle(R.id.list_view_study)
                        }
                    }
                }
            }
            //左侧单词列表
            click(R.id.page_right_up){
                toggle(R.id.list_view_study)
            }
        }
    }

}

fun StudyWord(word: String) {
    val wordModel = Word.find(word)
    if(wordModel!=null) {
        StudyWord(LayoutJdc.box, wordModel)
    }else{
        msg("$word not find!")
    }
}
class StudyWord(box: ViewGroup, vItem: WordModel) : tabs(box, vItem) {
    init {
        ct = box.context
        //图片
        book.pic {imgFile->
            val dwa = Drawable.createFromPath(imgFile)
            img(R.id.word_img).apply{
                setBackgroundDrawable(dwa)
                visibility = if(Display.isHorizontalScreens.value) View.VISIBLE else View.GONE
                if(dwa==null) {
                    return@apply
                }
                setOnClickListener {
                    InfoDialog.show{
                        Image(
                            painter = rememberGlidePainter(request = imgFile),
                            contentDescription = "pic",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        Log.d("study_word: ","reload")
        box.apply {
            //单词显示
            findViewById<TextView>(R.id.word_name).text = vItem.word
            val tabs = linkedMapOf(
                //1 释义     shili
                R.id.word_type to wordContent(),
                //2 例句     liju
                R.id.juzi_content  to exampleContent(),
                //3 短语搭配 duanyudapei
                R.id.dydp_content  to dydpContent(),
                //7 同义词   tongyici
                R.id.tyic_content  to tyicContent(),
                //8 反义词   fanyici
                R.id.fyc_content  to fychContent(),
                //5 衍生词 yanshegnci
                R.id.ysc_content to yscContent(),
                //4 时态 cxbh
                R.id.cxbh_content  to cxbhContent(),
            )
            tabs.forEach {
                findViewById<ViewGroup>(it.key).apply {
                    removeAllViews()
                    addView(it.value)
                }
            }

            //加入生词本
            findViewById<View>(R.id.fl_vocab).setOnClickListener {
                GlobalVal.wordViewModel.insertBookmark(vItem)
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            }
            //口语评测
            findViewById<View>(R.id.kypc).apply {
                visibility = View.GONE
                setOnClickListener {

                }
            }

            //英式英语
            findViewById<View>(R.id.voicey_btn).setOnClickListener {
                if(!it.isPressed) return@setOnClickListener
                GlobalVal.tts.play(vItem.word,Locale.UK)
            }
            //音标
            var yb = vItem.meanings?.first()?.symbol
            if(yb==null) yb = listOf("","")
            if(yb.size==1) yb = listOf(yb[0],"")
            findViewById<TextView>(R.id.fayin_y).text = yb[0]

            //美式英语
            findViewById<View>(R.id.mei).setOnClickListener {
                if(it.isPressed) {
                    GlobalVal.tts.play(vItem.word, Locale.US)
                }
            }

            findViewById<TextView>(R.id.fayin_m).text = yb[1]

            //自然拼读
            findViewById<View>(R.id.ziran).apply {
                visibility = View.GONE
                setOnClickListener {

                }
            }
            //下三角指示
            val selects = findViewById<ViewGroup>(R.id.ll_triangle).children
            //内容
            val containers = findViewById<ViewGroup>(R.id.fl_container).children
            //真实内容
            val tvs = tabs.values.toList()
            var rIndex=0
            //tab 按钮
            findViewById<ViewGroup>(R.id.rg_tab).children.forEachIndexed { index, it ->
                val rows = tvs[index] as ViewGroup
                val sb = selects.toList()[index]
                if(rows.childCount==0){
                    it.visibility =View.GONE
                    if(sb.visibility == View.VISIBLE && index==0) {
                        Handler().postDelayed({
                            findViewById<ViewGroup>(R.id.rg_tab).children.first().performClick()
                        },200)
                    }
                    sb.visibility = View.GONE
                }else {
                    sb.visibility = View.VISIBLE
                    val reIndex=rIndex
                    it.visibility =  View.VISIBLE
                    it.setOnClickListener {
                        val rb = it as RadioButton
                        containers.forEachIndexed { j, c ->
                            c.visibility = if (index == j) View.VISIBLE else View.GONE
                        }
                        selects.forEachIndexed { j, s ->
                            (s as ViewGroup).children.first().visibility=if (index == j) View.VISIBLE else View.GONE
                        }
                    }
                }
            }
            Handler().postDelayed({
                GlobalVal.tts.play(vItem.word)
            },200)
//            findViewById<ViewGroup>(R.id.rg_tab).children.first().performClick()
        }
    }
}

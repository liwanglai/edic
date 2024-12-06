package com.ochess.edict.presentation.home.jdc.study_word

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ochess.edict.R
import com.ochess.edict.data.local.entity.Meaning
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.jdc.StudyWord


open class tabs(val box: ViewGroup, private val vItem: WordModel){
    var ct: Context? = null
    private fun mettingMap(layoutId:Int, orien:Int= LinearLayout.VERTICAL, initShow:(v: View, m: Meaning)->Boolean): LinearLayout {
        val rows = box(orien=orien)
        vItem.meanings?.forEachIndexed{index,m->
            val one =  LayoutInflater.from(ct).inflate(layoutId, null)
            if(initShow(one,m)) {
                rows.addView(one)
            }
        }
        return rows
    }
    fun box(v:View?=null,orien:Int= LinearLayout.HORIZONTAL): LinearLayout {
        val rows = LinearLayout(ct)
        rows.orientation = orien
        if(v!=null) rows.addView(v)
        return rows
    }
    @SuppressLint("MissingInflatedId")
    fun getOneView(pid:Int= R.layout.liju_content, id:Int= R.id.text1): View {
        val root = LayoutInflater.from(ct).inflate(pid, null)
        val v: View = root.findViewById(id)
        v.setPadding(10,2,10,2)
        (v.parent as ViewGroup).removeView(v)
        return v
    }
    //释义
    fun wordContent(): LinearLayout {
        val rt= mettingMap(R.layout.word_content) { it, m->

            it.apply {
                findViewById<TextView>(R.id.word_type_name).text = m.speechPart
                findViewById<TextView>(R.id.word_fanyi).text = if(m.def_ch ==null) m.def else m.def_ch + m.def
            }

            m.def!=null && m.def!!.isNotEmpty()
        }
        val firstbox = LayoutInflater.from(ct).inflate(R.layout.word_content,null)

        var a = vItem.ch?.split(".")
        if(a!=null) {
            a = listOf("")
        }else{
            a= listOf<String>()
        }
        if(a?.size!! <= 1) {
            a = arrayListOf("","")
            a[1] = vItem.ch.toString()
        }
        firstbox.findViewById<TextView>(R.id.word_type_name).text = a[0]
        firstbox.findViewById<TextView>(R.id.word_fanyi).text = a[1]

        rt.addView(firstbox,0)
        return rt
    }
    /**
     * 例句
     */
    fun exampleContent(): View? {
        return mettingMap(R.layout.liju_content) { it, m->
            it.apply {
                findViewById<TextView>(R.id.liju_eng).text =m.example
                findViewById<TextView>(R.id.liju_chn).text =m.example_ch
            }
            !m.example_ch.isNullOrEmpty() || (m.example!=null && m.example.isNotEmpty())
        }
    }
    //短语搭配
    fun dydpContent(): LinearLayout {
        return mettingMap(R.layout.dydp_content) { it, m->
            var dy = ""
            m.phrase?.forEach{
                dy+=it+"\n\n"
            }
            it.apply {
                findViewById<TextView>(R.id.dydp).text = dy
            }
            dy.length>0
        }
    }


    /**
     * 衍生词
     */
    fun yscContent(): View? {
        return mettingMap(R.layout.ysc_content,LinearLayout.HORIZONTAL) { it, m->
            it.apply {
                val ids = arrayOf(R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text6)
                ids.forEach {
                    findViewById<TextView>(it).visibility = View.GONE
                }
                m.derived?.forEachIndexed {index,it->
                    if(index>=ids.size){
                        val v = getOneView() as TextView
                        v.text = it
                        val p = findViewById<TextView>(ids[0]).parent as ViewGroup
                        p.addView(v)
                    }else {
                        findViewById<TextView>(ids[index]).apply {
                            text = it
                            visibility = View.VISIBLE
                        }
                    }
                }
            }
            m.derived!=null && m.derived.size > 0
        }
    }

    /**
     * 同义词
     */
    fun tyicContent(): View? {
        val reSet = hashSetOf<String>()
        return mettingMap(R.layout.tyic_content,LinearLayout.HORIZONTAL) { it, m->
            val p = it.findViewById<TextView>(R.id.text1).parent.parent as ViewGroup
            p.removeAllViews()
            m.synonyms?.forEach { word->
                val box = getOneView(R.layout.tyic_content, R.id.textbox) as ViewGroup
                val v =box.findViewById<TextView>(R.id.text1)
                v.text = word
                v.setOnClickListener(View.OnClickListener {
                    StudyWord(word)
                })
                if(!reSet.contains(word)) {
                    p.addView(box)
                }
                reSet.add(word)
            }

            m.synonyms != null && m.synonyms.size > 0
        }
    }
    /**
     * 反义词
     */
    fun fychContent(): View? {
        return mettingMap(R.layout.fyc_content,LinearLayout.HORIZONTAL) { it, m->
            val p = it.findViewById<TextView>(R.id.text1).parent.parent as ViewGroup
            p.removeAllViews()
            m.antonym?.forEach {word->
                val box = getOneView(R.layout.fyc_content, R.id.textbox) as ViewGroup
                val v = box.findViewById<TextView>(R.id.text1)
                v.text = word
                v.setOnClickListener(View.OnClickListener {
                    StudyWord(word)
                })
                p.addView(box)
            }
            !m.antonym.isNullOrEmpty()
        }
    }

    //时态
    fun cxbhContent(): View? {
        return mettingMap(R.layout.cxbh_content) { v, m->
            val p = v as LinearLayout
            p.removeAllViews()
            p.orientation = LinearLayout.HORIZONTAL
            m.tense?.forEach{
                val b = LayoutInflater.from(ct).inflate(R.layout.cxbh_content, null).apply {
                    findViewById<TextView>(R.id.type).text = it.key
                    findViewById<TextView>(R.id.text).text = it.value
                }
                p.addView(b)
            }

            m.tense!=null && m.tense.size > 0
        }
    }
}
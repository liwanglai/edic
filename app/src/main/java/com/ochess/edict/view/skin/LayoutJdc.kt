package com.ochess.edict.view.skin

import android.content.Context
import android.database.DataSetObserver
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import com.ochess.edict.R
import com.ochess.edict.presentation.home.PAGE_FROM_BOOKMARK
import com.ochess.edict.presentation.main.components.Display.mtCnReplace
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun


class LayoutJdc {
    class layouts{
        companion object {
            //书本选择
            val books: Int = R.layout.activity_filelist
            //生词本
            val bookmark: Int = R.layout.activity_vocab
            //听写
            val listen: Int = R.layout.activity_dictation_word
            //单词单选
            val select: Int = R.layout.activity_recite_word
            //主页
            val home: Int = R.layout.activity_study_word
            //功能选择
            val main: Int = R.layout.activity_main2
        }
        object methods {
                fun hides(vararg ids:Int) {
                    ids.forEach {
                        hide(it)
                    }
                }
                fun hide(id: Int) {
                    id(id).visibility = View.GONE
                }
                fun shows(vararg ids:Int) {
                    ids.forEach {
                        show(it)
                    }
                }
                fun show(id: Int,s:Boolean = true) {
                    if(!s) return hide(id)
                    id(id).visibility = View.VISIBLE
                }
                fun toggle(id:Int){
                    show(id,id(id).visibility == View.GONE)
                }
                fun id(id: Int): View {
                    return Views.root.findViewById(id)
                }
                fun <T> id(id: Int): T {
                    return Views.root.findViewById<View>(id) as T
                }
                fun click(id:Int,f:(v:View)->Unit){
                    id(id).setOnClickListener(f)
                }
                fun img(id:Int):ImageView{
                    return id<ImageView>(id)
                }
                fun clicks(vararg ids:Int,f:(i:Int)->Unit) {
                    ids.forEachIndexed{index,id->
                        id(id).setOnClickListener{
                            f(index)
                        }
                    }
                }
                fun clicks(ids:List<Int>,f:(i:Int)->Unit) {
                    ids.forEachIndexed{index,id->
                        id(id).setOnClickListener{
                            f(index)
                        }
                    }
                }
                fun radio(ids:List<Int>,index:Int){
                    ids.forEachIndexed{i,id->
                        id(id).isSelected = (id == index)
                    }
                }
                fun layout(id:Int) :ViewGroup{
                    return LayoutInflater.from(Views.root.context).inflate(id, null) as ViewGroup
                }
                fun row(id:Int=0):LinearLayout{
                    if(id==0) {
                        return LinearLayout(Views.root.context)
                    }
                    return id<LinearLayout>(id)
                }

                /**
                 * 基本视图元素
                 * pid layoutid
                 * id需要摘取的元素id
                 */
                fun <T:View?>bv(pid:Int, id:Int): T {
                    val root = LayoutInflater.from(Views.root.context).inflate(pid, null)
                    val v: View = root.findViewById(id)
                    (v.parent as ViewGroup).removeView(v)
                    return v as T
                }
                fun text(id:Int,t:String){
                    id<TextView>(id).text = mtCnReplace(t)
                }
        }
    }
    class Views(val id:Int){
        companion object {
            lateinit var root: View
        }
        @Composable
        fun onInit(onAddEvents:(View)->Unit){
            val bgColor = MaterialTheme.colorScheme.background.value.toInt()
            AndroidView(
                factory = {
                    root = LayoutInflater.from(it).inflate(id, null)
                    root
                }
            ) {
                it.setBackgroundColor(bgColor)
                onAddEvents(it)
            }
        }

        class Adapter(private val ctx: Context,
                      private val layoutId:Int, val onBindViewHolderDo:(ViewGroup, Any, Int)->Unit) : RecyclerView.Adapter<Adapter.VH>(),
            ListAdapter {
            private val list = arrayListOf<Any>()
            private val viewAll = arrayListOf<View>()
            private val inflater: LayoutInflater = LayoutInflater.from(ctx)

            class VH(val binding:  View) : RecyclerView.ViewHolder(binding)
            override fun onCreateViewHolder(vg: ViewGroup, pos: Int): VH {
//                val binding = DataBindingUtil.inflate<LayoutBaseItemsBinding>(
//                    inflater, layoutId, vg, false
//                )
                val binding = LayoutInflater.from(ctx).inflate(layoutId, vg, false)
                return VH(binding)
            }

            override fun onBindViewHolder(vh: VH, pos: Int) {
                onBindViewHolderDo(vh.binding as ViewGroup,list[pos],pos)
            }

            override fun getItemCount(): Int = list.size
            fun setItems(data:ArrayList<Any>) {
                list.clear()
                list.addAll(data)
                notifyDataSetChanged()
            }

            override fun registerDataSetObserver(p0: DataSetObserver?) {

            }

            override fun unregisterDataSetObserver(p0: DataSetObserver?) {

            }

            override fun getCount(): Int {
                return list.size
            }

            override fun getItem(p0: Int): Any {
                return list[p0]
            }

            override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
                if(p0 < viewAll.size){
                    return viewAll.get(p0)
                }
                val view = View.inflate(
                    p2?.context,
                    layoutId,
                    null
                )
                onBindViewHolderDo(view as ViewGroup,list[p0],p0)
                viewAll.add(view)

                return view
            }

            override fun getViewTypeCount(): Int {
                return 1
            }

            override fun isEmpty(): Boolean {
                return list.size ==0
            }

            override fun areAllItemsEnabled(): Boolean {
                return true
            }

            override fun isEnabled(p0: Int): Boolean {
                return true
            }
        }


    }


    companion object {
        lateinit var box:ViewGroup
        @Composable
        fun view(id:Int,onInitEvent:(ViewGroup)->Unit){
            val v = Views(id)
            v.onInit{
                box = it as ViewGroup
                onInitEvent(it as ViewGroup)
            }
        }

        /**
         * 播放正确声音或错误声音
         */
        fun playStatus(ok: Boolean) {
            val id = if(ok) R.raw.correct else R.raw.wrong
            val act = ActivityRun.context
            val uri = Uri.parse(("android.resource://" + act.packageName) + "/" + id)
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(act,uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {

            }
//            val f = ActivityRun.context.resources.openRawResourceFd(id)
//            Audio.play(f){
//
//            }
        }

        fun goHome() {
//            NavScreen.openJdc(0)
            NavScreen.openHome(PAGE_FROM_BOOKMARK)
        }
    }
}
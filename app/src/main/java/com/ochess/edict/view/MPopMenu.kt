package com.ochess.edict.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.config.SpeakConf
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.BookItemType
import com.ochess.edict.presentation.main.components.Display.mt

/**
 * 弹框
 * example
 *  var mpm = MPopMenu(arrayMapOf(
 *         "播放速度" to "0",
 *         "语种" to "English"
 *     ))
 *     mpm.add()
 *     SpeakButton(clicked = clickedState, modifier = mpm.modifier
 *         .combinedClickable(
 *             onLongClick = {
 *                 mpm.show {
 *
 *                 }
 *             }
 *         )
 *   )
 */
class MPopMenu(var items: ArrayList<dataClass>) {
    var visible by  mutableStateOf(false)
    var offset:DpOffset by mutableStateOf( DpOffset(0.dp,0.dp) )
    lateinit var itemClickFun:(k:String,v:dataClass) -> Unit
    lateinit var target:Any
    var modifier: Modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                offset = DpOffset(
                    it.x.toDp(),
                    it.y.toDp()
                )
            }
        )
    }
    constructor(itema: List<dataClass>) : this(arrayListOf<dataClass>()) {
        items.addAll(itema)
    }
    private fun onItemClick(k:String,v:dataClass){
        return itemClickFun(k,v)
    }
    @Composable
    fun add() {
        DropdownMenu(expanded = visible,
            offset = offset,
            onDismissRequest = {
                visible = false
        }) {
            menuContent()
        }
    }
    @Composable
    fun menuContent(){
        var secondLevelExpanded by remember { mutableStateOf(false) }
        var childList:ArrayList<dataClass> = ArrayList()
        var keyName=""
        for (item in items) {
            val key = item.title
            DropdownMenuItem(
                onClick = {
                    if (item.child.size > 0) {
                        childList = item.child
                        keyName = item.title
                        secondLevelExpanded = true
                    } else {
                        onItemClick(key, item)
                        visible = false
                    }

                },
                content = {
                    val c = if(item.selected) MaterialTheme.colors.primary else Color.Black
                    Text(key, color = c)
                },
            )
            if (secondLevelExpanded) {
                DropdownMenu(
                    expanded = secondLevelExpanded,
                    onDismissRequest = {
                        secondLevelExpanded = false
                    }
                ) {
                    childList.forEach {
                        DropdownMenuItem(onClick = {
                            onItemClick(keyName, it)
                            visible = false
                            secondLevelExpanded = false
                        }) {
                            Text(it.title)
                        }
                    }
                }
            }
        }

    }
    fun show(callBack: (k:String,v:dataClass) -> Unit) {
        itemClickFun = callBack
        visible = true
    }

    fun bindElement(item: Any): MPopMenu {
        target = item
        return this
    }

    fun upItems(bmChild: List<dataClass>): MPopMenu {
        return upItems(bmChild as ArrayList)
    }
    fun upItems(bmChild: ArrayList<dataClass>): MPopMenu {
        items = bmChild
        return this
    }

    /**
     * 更新语言显示
     */
    fun upMtTitle(): MPopMenu {
        items.forEach {
            if(!it.title.matches(Regex("^\\d+$")))
            it.title = mt(it.title)
        }
        return this
    }

    data class dataClass(var title:String="", val name:String="", var value: Any=0, val child:ArrayList<dataClass> = arrayListOf(), var selected:Boolean=false)


    companion object {
        val Unspecified: MPopMenu = MPopMenu(arrayListOf())
        val speakItems = arrayListOf(
            dataClass("播放", "play"),
            dataClass("重复播放", "dplay")
        )
        fun HistoryTypes(): MPopMenu {
            return MPopMenu(arrayListOf(
                dataClass("History", value = 0),
                dataClass("BookHistory", value = 1),
                dataClass("TestHistory", value = 2)
            )).upMtTitle()
        }

        fun SpeakConfigMenu(config:SpeakConf= SpeakConf()) :MPopMenu {
            val items = speakItems.clone() as ArrayList<dataClass>
            config.forEach {
                items.add(it)
            }
            items.add(dataClass("听书","listenBook"))
            //items.putAll(SpeakConf.options as ArrayMap<String,Any>)
            return MPopMenu(items).upMtTitle()
        }

        fun categoryEditMenu(item: BookItem):MPopMenu{
            val rt = MPopMenu(arrayListOf(
                dataClass("首页打开","toMain"),
                dataClass("听书页打开","toBook"),
//                dataClass("选择","select"),
                dataClass("剪切","cut"),
            ))
            //文章和目录共有的特殊菜单
            if(item.type != BookItemType.word) {
                rt.items.addAll(listOf(
                    dataClass("删除","del"),
                    dataClass("重命名","rename"),
                ))
            }
            //各自的特殊菜单
            when(item.type){
                BookItemType.word ->
                    rt.items //.add(dataClass("重命名","rename"))
                BookItemType.category ->
                    rt.items.add(dataClass("导出","save"))
                BookItemType.article ->
                    rt.items.add(dataClass("编辑","edit"))
            }
            rt.bindElement(item)
            return rt.upMtTitle()
        }

        fun BookAddMenu(): MPopMenu {
            return MPopMenu(arrayListOf(
                dataClass("添加分类","open"),
                dataClass("添加文件","openFile"),
//                dataClass("默认功能","default"),
//                dataClass("搜索","search"),
                dataClass("选择","select"),
            )).upMtTitle()
        }

        fun ViewModeMenu() :MPopMenu {
            val rt= MPopMenu(arrayListOf(
//                dataClass("记单词","jdc",MenuConf.mode.jdc),
//                dataClass("WordStudy","WordStudy",MenuConf.mode.wordStudy),
//                dataClass("ListenBook","ListenBook",MenuConf.mode.listenBook),
//                dataClass("WordGame","WordGame",MenuConf.mode.findGame),
//                dataClass("Print","Print",MenuConf.mode.print),
            ))
            val itemGroups = MenuConf.modeGroups()
            itemGroups?.forEach {
                it.value.forEach {
                    rt.items.add(dataClass(it.name, it.name, it.ordinal))
                }
            }
            return rt
        }
    }
}
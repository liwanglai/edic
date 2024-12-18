package com.ochess.edict.presentation.bookmark


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.collection.arrayMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.buildAnnotatedString
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Category
import com.ochess.edict.presentation.bookmark.data.BookItem
import com.ochess.edict.presentation.bookmark.data.BookItemType
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import com.ochess.edict.presentation.home.PAGE_FROM_BOOKMARK
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.presentation.main.components.Confirm
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.components.InputDialog
import com.ochess.edict.presentation.main.components.Prompt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.view.MPopMenu
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem.Type as cType
import com.ochess.edict.presentation.bookmark.data.BookMark.clickModelItem
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.bookItems
import com.ochess.edict.presentation.bookmark.data.BookMark.Companion.clickModel
import com.ochess.edict.presentation.bookmark.data.BookMark
import com.ochess.edict.presentation.history.BookHistroy


class BookMarkEvent {
    companion object {
        //当前书本的id
        var bookId: Int=0
        lateinit var cutData: List<BookItem>
        val navController = GlobalVal.nav
        var openItem: BookItem? = null
        fun AddButtonOnLongClick(m: MPopMenu) {
            m.show { k, v ->
                when(v.name){
                    "open" ->
                        InputDialog.show()
                    "openFile" ->
                        FileUtil.pickFile(onReadFile = { data, file, uri->
                            Article.add(data,file,BookMark.pid)
                            Run.upBookItems()
                        })
                    "setClickRun" ->{
                           ActivityRun.msg("待开发")
                    }
                    "search" ->
                        GlobalVal.isSearchVisible.value = true
                    "select" ->
                        clickModel=clickModelItem.isSelect
                }
            }
        }
        fun ItemOnClick(item: BookItem) {
            when (clickModel) {
                clickModelItem.click ->
                    Run.open(item)
                clickModelItem.isSelect ->
                    item.changeSelecte()
                clickModelItem.isCanCopy -> {
                    when(item.type) {
                        BookItemType.category ->
                            Run.open(item)
                        BookItemType.article->
                            onPaste(item.pid,item)
                        else->{

                        }
                    }
                }
            }
        }

        fun ItemonLongClick(menu: MPopMenu) {
//            menu.offset()
            val item: BookItem = menu.target as BookItem
            menu.show { k, v ->
                when (v.name) {
                    "toMain" ->
                        Run.open(item,true)
                    "toBook" ->
                        Run.openBook(item)
                    "select" -> {
                        item.changeSelecte()
                        clickModel = clickModelItem.isSelect
                    }
                    "jump" ->
                        BookMark.changePid(v.name.toInt())
                    "cut" -> {
                        cutData = listOf(item)
                        clickModel = clickModelItem.isCanCopy
                        if(item.type == BookItemType.word) {
                            BookMark.changePid(0)
                        }
                    }
                    "del" -> {
                        Confirm.show{
                            item.delete()
                            Run.upBookItems()
                        }
                    }
                    "rename" -> {
                        Prompt.show(item.name) {
                            item.save(name = it)
                            Run.upBookItems()
                        }
                    }
                    "save" ->
                        Run.saveToFile(item)
                    "edit" ->{
                        InputDialog.show(eObj=item)
                        Article.getContent(item.id){
                            InputDialog.setText(it)
                        }
                    }
                }
            }
        }

        fun onPaste(pid:Int,item:BookItem?=null) {
            val words = arrayListOf<BookItem>()
            cutData.forEach{
                if(it.type == BookItemType.word) {
                    words.add(it)
                }else {
                    it.save(pid = pid)
                }
            }
            if(words.size>0){
                if(item==null) {
                    Prompt.show(mt("默认分类")) {
                        val textWords = words.joinToString("\n") { it.name }
                        Article.add(textWords, it, pid)
                        words.forEach {
                            it.delete()
                        }
                        Run.upBookItems()
                    }
                }else{
                    item.save(add=words)
                    words.forEach {
                        it.delete()
                    }
                    Run.upBookItems()
                }
            }

            clickModel = clickModelItem.click
            Run.upBookItems()
        }

        fun onCheckAll(items: SnapshotStateList<BookItem>) {
            items.forEach{
                it.isSelect = true
            }
        }

        fun ItemOnSlectChange(item: BookItem) {
            if(!item.isSelect){
                val selected = bookItems.filter { it.isSelect }
                if(selected.size==0){
                    clickModel = clickModelItem.click
                }
            }
        }

        fun onAdd(textContent: String, pid:Int) {

            val rows = textContent.split(Regex("\\n"))
            if(rows.size>1) {
                //是文章
                if (!rows[1].matches(Regex("[\u4E00-\u9FA5]+"))) {
                    val connect = rows.subList(1,rows.size).joinToString()
                    Article.add(connect,rows[0],pid)
                }else {
                    rows.forEach {
                        if (it.length > 0) {
                            Category.add(it, pid)
                        }
                    }
                }
                Run.upBookItems()
                return
            }
            Category.add(textContent, pid)
            Run.upBookItems()
        }

        fun onDeleteItems(items: List<BookItem>) {
            Confirm.show{
                items.forEach {
                    it.delete()
                }
                Run.upBookItems()
            }
        }

        fun onCommonItemClick(name: VirtualCommonItem) {
            when(name.type){
                cType.level ->
                    NavScreen.LevelScreen.open()
                cType.online ->
                    NavScreen.ListenBooks.open()
                else ->
                    BookMark.changePid(name.id)
            }
        }


        val Run = EventRun()
    }

    class EventRun {

        fun openBook(item: BookItem) {
            when(item.type) {
                BookItemType.article -> {
                    val ids = item.id.toString()
                    navController.navigate("${NavScreen.routes.ListenBooks}?aids=" + ids) {
                        launchSingleTop = true
                    }
//                    ActivityRun.start(
//                        ListenBookActivity::class.java.name,
//                        arrayListOf(item.id.toString())
//                    )
                }
                BookItemType.category -> {
                    Category.getArticles(item.id){
                        //首页打开条目
                        val ids = it.map{it.id}.joinToString(",")
                        Run.byMainThrend {
                            navController.navigate("${NavScreen.routes.ListenBooks}?aids=" + ids) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
                BookItemType.word ->
                    //首页打开条目
                    navController.navigate(NavScreen.routes.ListenBook) {
                        launchSingleTop = true
                    }
            }

        }

        fun open(item: BookItem,absToMain:Boolean=false) {
//目录的单击直接进入
            if (item.type == BookItemType.category && !absToMain) {
                BookMark.changePid(item.id)
                return
            }
            val jump = {
                //首页打开条目
                try{
                    navController.navigate("${NavScreen.HomeScreen.route}?wordIndex=${item.index}?fromPage=$PAGE_FROM_BOOKMARK?level=-1") {
                        launchSingleTop = true
                    }
                }catch (e:Exception){
                    Log.d( "open",e.toString())
                }
            }
            openItem = item
            //文章更新单词列表
            when(item.type) {
                BookItemType.article -> {
                    BookHistroy.add(item)
                    BookConf.usBook(item.id)
                    NavScreen.refrash()
                    if(!item.inited){
                        item.initStatusText = mt("下载中")
                        val wds = Article.find(BookConf.instance.id)?.findWords()
                        TTSListener.mp3Create(wds) {okCount->
                            if(okCount>0) {
                                item.inited = true
                                item.save()
                                item.initStatusText = mt("下载完成")
                            }
                            jump()
                        }
                    } else {
                        jump()
                    }
                }
                BookItemType.category -> {
                    Category.getArticles(item.id, true) {
                        val rt = arrayListOf<String>()
                        it.forEach {
                            rt.addAll(it.findWords())
                        }
                        GlobalVal.bookmarkViewModel.upListByWords(rt) {
                            if (it > 0) Run.byMainThrend { jump() }
                        }
                    }
                }
                BookItemType.word -> {
                    GlobalVal.bookmarkViewModel.selectWord(item.name)
                    jump()
                }
            }
        }

        /**
         * 保存到剪切板
         */
        fun saveToFile(item: BookItem) {
            val rt = arrayListOf<String>()
            Category.getArticles(item.id,true){
                it.forEach {
                    rt.addAll(it.findWords())
                }
                val text = rt.joinToString("\n")
                if(text.length>0) {
                    Run.byMainThrend{
                        GlobalVal.clipboardManager.setText(buildAnnotatedString {
                            append(text)
                        })
                        Toast.makeText(ActivityRun.context, mt("Copied"), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //upBookItems函数会被莫名进程重复调用 导致数组被多进程同时赋值 报错 添加这个锁
        private val mutex = Mutex()
        @SuppressLint("SuspiciousIndentation")
        val cachePids = arrayMapOf<Int,List<BookItem>>()
        fun upBookItems(){
            MainScope().launch {
                upItems()
            }
        }
        suspend fun upItems(){
            mutex.lock()
            val pid = BookMark.pid
            Log.d("upBookItems: ","$pid")
            val bookItems =arrayListOf<BookItem>()
            try{
                bookItems.clear()
                if(pid>-1) {
                    Category.getByPid(pid) {
                        bookItems.addAll(it.map {
                            BookItem.build(it)
                        })
                        Article.getByCid(pid) {
                            it.forEach {
                                val item = BookItem.build(it)
                                bookItems.add(item)
                            }
                            BookMark.setBookItems(bookItems)
                        }
                    }

                }
                else if(pid==-1){
                    BookMark.setBookItems(bookItems)
                    GlobalVal.bookmarkViewModel.getAll {
                        it.forEach {
                            val item = BookItem.build(it)
                            bookItems.add(item)
                        }
                        BookMark.setBookItems(bookItems)
                    }
                }else if(pid in listOf(-4)){
                    Article.getByCid(pid) {
                        it.forEach {
                            val item = BookItem.build(it)
                            bookItems.add(item)
                        }
                        BookMark.setBookItems(bookItems)
                    }
                }
            }finally {
                mutex.unlock()
            }
        }
        private fun byMainThrend(function: () -> Unit) {
            ActivityRun.runOnUiThread{
                function()
            }
//            Looper.prepare()
//            Handler(Looper.getMainLooper()).post {
//                function()
//            }
        }
    }
}
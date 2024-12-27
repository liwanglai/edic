package com.ochess.edict.presentation.home.game.wordScapes

import android.os.Handler
import android.util.Log
import androidx.collection.arrayMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.ochess.edict.data.model.Article
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.game.findedWords
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.max
import kotlin.math.min

open class WordScapesData {
    var name:String=""
    //所有可用的单词列表
    var inwords: List<String> = listOf<String>()
    //空单词对象
    val emptyWordModel = WordModel(word = name, level = -1, wordsetId = "",ch=null, meanings = null)
    //下一个单词的提示信息
    val nextWord: MutableStateFlow<WordModel> = MutableStateFlow<WordModel>(emptyWordModel)


    //20*20的矩阵
    var box = arrayListOf<ArrayList<Char>>()

    //矩阵中可以扩展的点
    var canExts = arrayListOf<IntOffset>()
    //单词和位置的映射
    var wordMap = arrayMapOf<String, wordPos>()
    //随机发生器
    var rand = Random()

    ////输入圆盘
    //所有圆点 输入字母的点
    var cPoints = arrayListOf<charPos>()
    //dragPoing 拖拽点
    var drPoint = mutableStateOf( charPos(0.0, 0.0))
    //当前划过的单词
    var nowWord = ""
    //最后焦点光标是否在点里
    var lastInPoint = false
    //单词的字符串到model对象的hash字典
    var wordModelMaps = linkedMapOf<String, WordModel>()
    //输入圆盘的字母列表
    val inCircleChars =  mutableStateOf(listOf<Char>())
    //多次使用的点列表
    val multipleUsedPoint = arrayListOf<IntOffset>()
    val maxY=50
    val maxX=12
    /**
     * 矩阵单词位置
     * @param x 横向索引
     * @param y 纵向索引
     * @param z 方向 0横向 1 横向
     * @param finded 是否找到
     */
    data class wordPos(val x:Int, val y:Int, val z:Int,var finded:Boolean=false)

    /**
     * 输入圆盘字母偏移坐标
     * @param x 屏幕坐标横向
     * @param y 屏幕坐标竖向
     * @param c 字母
     */
    data class charPos(var x: Double, var y: Double, val c:Char=Char(0)){
        fun toPoint(): Offset {
            return Offset(x.toFloat(),y.toFloat())
        }
    }
    var onFinished = {}
    fun onFinish(overFun:()->Unit){
        onFinished = overFun
    }
    fun onFinish(){
        onFinished()
    }
    /**
     * 初始化配置一个单词
     * @param it 单词
     * @param a 矩阵x
     * @param b 矩阵y
     * @param c 单词方向
     *
     */
    private fun setOne(it:String,a:Int,b:Int,fx:Int) :Boolean{
        var x = a
        var y = b
        val sets = arrayListOf<IntOffset>()
        //当前单词的字母是否复用
        val posUseds = arrayListOf<Boolean>()
        fun callback(){
            while (!sets.isEmpty()) {
                x = sets[0].x
                y = sets[0].y
                sets.removeAt(0)
                val reset = !posUseds[0]
                posUseds.removeAt(0)
                if(reset) {
                    box[x][y] = '0'
                }

                multipleUsedPoint.remove(IntOffset(x,y))
            }
        }
        //循环单词字母
        for (i in 0..it.length - 1) {
            //需要占领的位置有数据存在 并且数据和当前单词的数据不相同就废弃
            if(box[x][y] != '0' && box[x][y] != it[i]) {
                callback()
                return false
            }
            val p = IntOffset(x,y)
            sets.add(p)
            //共同点信息添加
            if(box[x][y] == it[i] && multipleUsedPoint.indexOf(p)==-1){
                multipleUsedPoint.add(p)
            }
            //共用点检查
            posUseds.add(box[x][y] != '0')
            box[x][y] = it[i]
            if (fx == 0)
                x++
            else
                y++
        }

        val posUseds2= posUseds.map { it } as ArrayList
        //posUseds 继续扩展临近节点
        sets.forEachIndexed {i,it->
            if(!posUseds[i]) {
                val around = listOf(
                    if(it.x-1>=0)  box[it.x-1][it.y] else '0',
                    if(it.y-1>=0)  box[it.x][it.y-1] else '0',
                    if(it.x+1<maxX)  box[it.x+1][it.y] else '0',
                    if(it.y+1<maxY)  box[it.x][it.y+1] else '0',
                )
                posUseds2[i] = when(fx) {
                    0 -> around[1]!='0' || around[3]!='0'
                    1 -> around[0]!='0' || around[2]!='0'
                    else -> false
                }
            }
        }

        //共用数据检查 如果连续相同就重新找
        val posUNA = arrayListOf(0)
        posUseds2.forEach {
            if(it) {
                posUNA.set(posUNA.lastIndex,posUNA.last()+1)
            }else{
                posUNA.add(0)
            }
        }
        val posUN = posUNA.maxOrNull()
        Log.d("setwords: ","$sets,$posUseds,$posUN")
        if(posUN!! >1){
            callback()
            return false
        }

        ////新增完单词之后再统计一次可扩展节点
        //交叉点和周边四个都去掉可扩展功能
        val rmCommonUsed = arrayListOf<IntOffset>()
        sets.forEach {
            if(canExts.contains(it)){
                rmCommonUsed.add(it)
                canExts.remove(it)
            }else {
                canExts.add(it)
            }
        }
        rmCommonUsed.forEach {
            val around= listOf(
                IntOffset(it.x-1,it.y),
                IntOffset(it.x,it.y-1),
                IntOffset(it.x+1,it.y),
                IntOffset(it.x,it.y+1),
            )
            val pIn = around.filter {
                if(it.x>=0 && it.x<maxX && it.y>=0 && it.y<maxY)
                    box[it.x][it.y] != '0'
                else
                    false
            }
            when(pIn.size){
                3 ->{
                    val a =pIn.first()
                    val b = pIn.toList()[1]
                    val c = pIn.last()
                    if(a.x==b.x || a.y==b.y){
                        canExts.remove(c)
                    }else if(a.x==c.x || a.y==c.y){
                        canExts.remove(b)
                    }else{
                        canExts.remove(a)
                    }
                }
                4 -> {
                    canExts.removeAll(around)
                }
                else ->{}
            }



        }
        //扩展点新增 上下左右四个点有两个以上点空闲就是可扩展点
        val rmAll = canExts.filter {
            val ball= listOf(
                if (it.y + 1 < maxX-1) box[it.x][it.y + 1] else '0',
                if (it.y - 1 >= 0) box[it.x][it.y - 1] else '0',
                if (it.x + 1 < maxY-1) box[it.x + 1][it.y] else '0',
                if (it.x - 1 >= 0) box[it.x - 1][it.y] else '0',
            )
            val b4 = ball.filter { it == '0' }.size
            b4<2
        }
        rmAll.forEach {
            canExts.remove(it)
        }

        return true
    }

    /**
     * 获取单词方向根据矩阵偏移
     *
     */
    private  fun getFxByPos(pos: IntOffset): wordPos? {
        wordMap.forEach {
            val p = it.value
            if(p.x != pos.x && p.y!=pos.y) return@forEach
            when(p.z){
                0 -> {
                    if(pos.y!=p.y) return@forEach
                    if(pos.x>=p.x && pos.x< p.x + it.key.length) return p
                }
                1 -> {
                    if(pos.x!=p.x) return@forEach
                    if(pos.y>=p.y && pos.y< p.y + it.key.length) return p
                }
            }
        }
        return null
    }
    fun initData(word:String){
        //输入圆盘的点
        cPoints = arrayListOf<charPos>()
        //最后的拖拽点
        drPoint.value= charPos(0.0, 0.0)
        //当前的单词
        nowWord = ""
        //最后输入在点中用于判断重复输入
        lastInPoint = false
        //单词到mode对象的映射
        wordModelMaps = linkedMapOf<String, WordModel>()
        //已经找到的单词列表
        findedWords.clear()

        var a = arrayListOf<Char>()
        for(c in name){
            a.add(c.toUpperCase())
        }
        val reLetters = arrayMapOf<Char,Int>()
        fun reCountLet(word:String){
            val letterNum = arrayListOf<Int>()
            val letter = arrayListOf<Char>()
            var last='0'
            word.forEach {
                if(last==it) {
                    letterNum.set(letterNum.lastIndex,letterNum.last()+1)
                }else{
                    letter.add(it)
                    letterNum.add(1)
                }
                last = it
            }
            letterNum.forEachIndexed{index,num->
                if(num>1) {
                    val c = letter[index]
                    val n = reLetters.get(c)
                    if(n==null){
                        reLetters.set(c,num)
                    }else if(n<num){
                        reLetters.set(c,num)
                    }
                }
            }
        }
        //重复字母添加
        inwords.forEach {
            reCountLet(it)
        }
        reLetters.forEach {
            val c = it.key.toUpperCase()
            for(i in 0..it.value-2){
                    a.add(c)
            }
        }

        inCircleChars.value = a
        //延迟获取单词对象hash
        Handler().postDelayed({
            Article.getWords(inwords) {
                it.map {
                    wordModelMaps[it.word] = it
                }
                if(word.length>0 && word in wordModelMaps) {
                    nextWord.value = wordModelMaps[word]!!
                    Thread.sleep(100)
                    GlobalScope.launch {
                        onFindWordAdd(word)
                    }
                }
            }
        },200)
    }
    /**
     * 根据当前已有的节点信息获取单词列表位置信息
     */
    fun getPostion(it:String,addRun:wordPos.()->Boolean): wordPos? {
        //可扩展字符寻找可扩展位置 确定x,y,fx的值
        if(canExts.size > 0) {
            for(posindex in 0..canExts.size-2) {
                val pos = getExtPos(it,posindex)
                if(pos == null) continue

                if(addRun.invoke(pos)) {
                    Log.d("setWord: ","单词($it) 扩展字符寻找ok：<$pos,$posindex/${canExts.size}>")
                    return pos
                }
            }
        }
        //随机100个位置尝试
        var mx = 1
        var my = 0
        if(canExts.size>0){
            mx = canExts.maxOf { it.x }+2
            my = canExts.maxOf { it.y }+2
        }
        var tryCount = 100
        while(tryCount-- > 0){
            val pos = getRandPos(it,mx,my)
            if(addRun.invoke(pos)) {
                Log.d("setWord: ","单词($it) 随机位置寻找ok：<$pos>")
                return pos
            }
        }

        return null
    }
    fun getRandPos(it:String,xs:Int=1,ys:Int=0): wordPos {
        var x = rand.nextInt(max(1,it.length/2))+1
        var y = rand.nextInt(it.length)
        var fx = rand.nextInt(1)
        //如果单词长度超过10个则默认竖向
        if (fx == 0 && x + it.length > 10) {
            fx = 1
        }
        if(fx==0){
            y+=ys
        }else{
            x+=xs
        }
        x= min(x,maxX)
        y= min(y,maxY)
        return wordPos(x,y,fx)
    }
    fun getExtPos(it:String,posindex:Int): wordPos? {
            val pos = canExts[posindex]
            val c = box[pos.x][pos.y]
            val posWord = getFxByPos(pos)
            //获取单词方向
            var fx = when(posWord?.z) {
                0-> 1
                1-> 0
                else -> 0
            }
        Log.d("getExtPos: ","$it,$pos,$posWord,$fx")
            var x = pos.x
            var y = pos.y
            var i=0
            //循环单词寻找和当前可扩展点相同的单词 确定单词的新偏移坐标
            var X=x  //结束的点
            var Y=y
            for(itc in it) {
                if (itc == c) {
                    if (fx == 0) {
                        x -= i
                        X = x+it.length
                    } else {
                        y -= i
                        Y=y+it.length
                    }
                    break
                }
                i++
            }
            //有一个坐标小于0就重新寻找
            if(Math.min(x,y)<0 || Math.max(X,Y) > maxY){
                return null
            }
            return wordPos(x,y,fx)
    }
    /**
     * 初始化数据
     * name 单词字母去重排序后的字符串 也就是扩展表的name字段
     * allWords 单词的包含词
     * word当前单词
     */
    fun setWord(name: String, allWords: List<String>, word: String="") {
        this.name = name
        this.inwords = allWords
        initData(word)
        //12*50的矩阵
        box = arrayListOf<ArrayList<Char>>()
        for(i in 0..maxX){
            box.add(arrayListOf<Char>())
            for (j in 0..maxY){
                box[i].add('0')
            }
        }
        //矩阵中可以扩展的点
        canExts = arrayListOf<IntOffset>()
        //单词和位置的映射
        wordMap = arrayMapOf<String, wordPos>()
        //复用元素列表
        multipleUsedPoint.clear()

        //循环单词到矩阵
        inwords.forEach {
            val pos = getPostion(it){
                setOne(it, x, y, z)
            }

            if (pos!=null) {
                wordMap.put(it, pos)
                Log.d("setWord: ", "单词($it) 最终位置:<$pos>")
                outBox()
            }else {
                Log.d("setWord: ", "单词($it) 添加失败" )
            }
        }
    }

    private fun outBox() {
        val outString = arrayListOf<String>()
        box[0].forEach {
            outString.add("")
        }
        box.forEachIndexed{x,rows->
            rows.forEachIndexed{y,it->
                val c = if(it=='0') " " else it
                outString.set(y,outString.get(y)+c+",")
            }
        }
        val boxText = outString.filter { !it.matches(Regex("[ ,]+")) }.joinToString("\n")
        Log.d("setWord: ",boxText)
    }

    fun refrash() {
        setWord(name, inwords)
    }
    fun resetNextWord(): Boolean {
        val noStudys = wordModelMaps.filter {
            !it.value.isStudyed
        }.values.toList()
        Log.d("resetNextWord: ","${noStudys.size}/${wordModelMaps.size}")
        if(noStudys.size>0) {
            val index = if(noStudys.size > 1 ) rand.nextInt(noStudys.size - 1) else 0
            nextWord.value = noStudys[index]
            return true
        }
        return false
    }

    fun onFindWordAdd(word:String){
        val index = findedWords.indexOf(word)
        if(index!=-1) {
            findedWords.removeAt(index)
        }
        findedWords.add(word)
        if(nextWord.value!=emptyWordModel) {
            HomeEvents.scapesGame.onFindWord(wordModelMaps[word])
        }
        wordModelMaps[word]?.isStudyed = true
        if(!resetNextWord()) {
            nextWord.value = emptyWordModel
            onFinish()
        }
    }
}
package com.ochess.edict.presentation.listenbook

import android.annotation.SuppressLint
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.collection.arrayMapOf
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochess.edict.R
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.listenbook.BookTools.Companion.ListenControl
import com.ochess.edict.presentation.listenbook.BookTools.Companion.ShowPage
import com.ochess.edict.presentation.listenbook.BookTools.Companion.StatPage
import com.ochess.edict.presentation.listenbook.BookTools.Companion.goBack
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.extend.MainRun
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.view.ClickAbelText
import com.ochess.edict.view.MPopMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.userAgent
import java.io.IOException
import java.util.Locale
import java.util.UUID

/**
 * 书本单词列表 带统计信息
 * 模拟多看的听书功能
 */
val lbd =  ListenBookData()
@Composable
fun ListenBookScreen(words: List<String>) {

    val args = words
    lbd.apply {
        if (args.size == 1) {
            if(sampleFileUrl != args[0]) {
                sampleFileUrl = args[0]
                loadTextFromUrl(sampleFileUrl)
            }
        } else {
            textToRead = args.map { it }
        }
        LaunchedEffect(textToRead) {
            if(textToRead.size>0) {
                bgRun {
                    initData(textToRead)
                }
            }
        }
        LaunchedEffect(dIndex) {
            if(scrollState!=null) {
                scrollState!!.animateScrollToItem(dIndex, -500)
                if(dIndex>0)
                UserStatus.defInterface.set("bookPos",dIndex)
            }
        }
        if(UserStatus.defInterface.get("bookPos")>0){
            MainRun(2000){
                dIndex = UserStatus.defInterface.get("bookPos")
            }
        }

        bColor = MaterialTheme.colorScheme.background
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
//                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize() // Remove padding
                    .padding(5.dp)
            ) {
                var menu = MPopMenu(ListenBookData.viewModes.values().map {
                    MPopMenu.dataClass(it.name,it.name,it)
                }).upMtTitle()
                var title by remember { mutableStateOf(UserStatus.get("ShowBookType","read")) }
                vMode = ListenBookData.viewModes.getByName(title)
                Row(modifier = Modifier.padding(10.dp,15.dp)) {
                    Text(
                        mt(title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            menu.show { k, v ->
                                vMode = v.value as ListenBookData.viewModes
                                title=v.name
//                                UserStatus.set{
//                                    it.putString("ShowBookType",title)
//                                    "ShowBookType"
//                                }
                            }
                        }
                    )
                    menu.add()
                }
                if(vMode == ListenBookData.viewModes.listen) {
                    if(sentencesQueue.size==0 && textToRead.size>0) bgRun {  initData(textToRead)}
                    ListenControl()
                }

               if(vMode != ListenBookData.viewModes.stat) {
                   ShowPage()
               }else{
                   if(sortedList.value.size==0 && textToRead.size>0) bgRun {
                       initData(textToRead)
                   }
                   StatPage()
               }
                goBack()
            }
        }
    }
}

class BookTools {
    companion object {
        @Composable
        fun ListenBookData.ListenControl() {
            Row (modifier = Modifier.alpha(0.8f).height(50.dp)){
                var buttonTitle by remember { mutableStateOf("AutoSpeak") }
                Button(onClick = {
                    isOpenSpeaking = !isOpenSpeaking
                    if (buttonTitle == "AutoSpeak") {
                        buttonTitle = "Speaking"
                        speakOut()
                    } else {
                        buttonTitle = "AutoSpeak"
                        tts.stop()
                    }
                }) {
                    Text(mt(buttonTitle)) // Set text color to soft white
                }
                Column(modifier = Modifier.padding(5.dp)) {
                    Text(
                        mt("播放速度") + ": ${
                            String.format(
                                "%.1f",
                                speechRate
                            )
                        }"
                    ) // Set text color to soft white
                    Slider(
                        value = speechRate,
                        onValueChange = {
                            speechRate = it
                            tts.setSpeechRate(speechRate)
                            if (isInitialized && tts.isSpeaking) {
                                // Stop speaking the current sentence
                                tts.stop()
                                // Reinitialize TTS with the new voice
                                // Continue speaking from the current index
                                continueSpeakingFromIndex(currentSentenceIndex)
                            }
                        },
                        valueRange = 0.1f..5.0f, // Extend the range to 5
                        steps = 50
                    )
                }
            }

            //                        Row{
//                            Column (modifier = Modifier.padding(1.dp)){
//                                Text(
//                                    "播放频率: ${
//                                        String.format(
//                                            "%.1f",
//                                            speechVolume
//                                        )
//                                    }"
//                                ) // Set text color to soft white
//                                Slider(
//                                    value = speechVolume,
//                                    onValueChange = {
//                                        speechVolume = it
//                                        tts.setPitch(speechVolume)
//                                    },
//                                    valueRange = 0.5f..2.0f, // Extend the range to 5
//                                    steps = 50
//                                )
//                            }
//                        }

        }

        /**
         * 文章内容
         */
        @Composable
        fun ListenBookData.ShowPage() {
            Box {
                scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState!!,
                    modifier = Modifier
                        .fillMaxSize() // Remove padding
                            .background(bColor)

                ) {
                    //每一段的输出
                    items(textToRead.size) {
                        textColors.add(bColor)
                        if (vMode == ListenBookData.viewModes.read) {
                            ClickAbelText(text = textToRead[it],
                                style = MaterialTheme.typography.titleSmall,
                                lineHeight = 30.sp,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
//                                modifier = Modifier.background(textColors[it])
                            )
                        } else {
                            Text(
                                text = textToRead[it],
                                modifier = Modifier
                                    .background(textColors[it])
                                    .fillMaxWidth()
                                    .clickable {
                                        continueSpeakingFromIndex(j2dMap[it])
                                    }
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun goBack() {
//                    IconButton(onClick = { onBackPressed() }, modifier = Modifier
//                        .background(MaterialTheme.colorScheme.background)
//                        .alpha(0.8f)
//                        .align(Alignment.BottomEnd)
//                        .padding(10.dp)
//                    ) {
//                        Icon(painter = painterResource(id = R.drawable.ic_back),
//                            contentDescription = null,
//                            tint = Color.Red)
//                    }
        }
        /**
         * 词频统计页
         */
        @Composable
        fun ListenBookData.StatPage() {
            Box {
                if(sortedList.value.size==0){
                    Column {
                        Text(mt("statisticsing")+"...",Modifier.padding(10.dp))
                        LinearProgressIndicator(
                            //0.0表示没有进度，1.0表示已完成进度
                            progress = progress.value,
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            color = Color.Green,
                        )
                    }
                }else {

                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxSize() // Remove padding

                    ) {
                        var sum = 0f
                        itemsIndexed(sortedList.value) {index,it->
                            Row (modifier = Modifier.clickable {
                                tts.speak(it.first,TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
                            }){
                                if(index==0) sum =0f;
                                sum+=it.second
                                val num = String.format("%.2f%%--%.2f%%(%d)",(it.second.toFloat()/docWordSum*100),(sum/docWordSum*100), it.second);
                                Text((index+1).toString()+".", modifier = Modifier.padding(0.dp,0.dp,5.dp,0.dp))
                                Text(it.first)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(num)
                            }
                        }
                    }
                }
            }
        }
    }
}
class ListenBookData :TextToSpeech.OnInitListener {

     var isOpenSpeaking: Boolean = false
     var sampleFileUrl = NetConf.resRoot+"/books/test.txt"

     var tts: TextToSpeech = TextToSpeech(ActivityRun.context, this)
     val sentencesQueue = arrayListOf<String>()
     //段落
     var textToRead  =listOf<String>()

     var speechRate by mutableStateOf(1.0f) // Normal speed is 1.0
     var speechVolume by mutableStateOf(1.0f) // Normal speed is 1.0


     var voice: Voice? = null
     var selectedVoice by mutableStateOf(voice)
     val availableVoices = mutableStateListOf<Voice>()

     var currentSentenceIndex = 0 // Track the index of the current sentence
     var isInitialized by mutableStateOf(false) // Track if TTS is initialized

     var textColors by mutableStateOf(arrayListOf<Color>())
     var dIndex by mutableStateOf(0)
     var j2dMap = arrayListOf(0)   //段落到首单词的hash映射
     var bColor: Color = Color.White
     var scrollState: LazyListState?=null

     val sortedList = mutableStateOf(listOf<Pair<String,Int>>())
     var docWordSum = 0
     val progress =  mutableStateOf(0f)
     enum class viewModes{
         listen,  //听书
         read,    //看书
         stat ;   //词频统计

         companion object {
             fun getByName(title: String): viewModes {
                for(m in viewModes.values()){
                    if(m.name.equals(title)) return m;
                }
                 return listen
             }
         }
     }
     //查看模式
     var vMode by mutableStateOf( viewModes.listen);

    fun statRun(textToRead:List<String>){
        var docPerCount = 0;
        val docPerSum = textToRead.size.toFloat()
        val words = arrayMapOf<String, Int>()
        docWordSum = 0
        textToRead.//去掉中文内容
            filter {
                !it.matches(Regex(".+[\u4E00-\u9FA5]+.+"))
            }.forEach {
            val allWords = it.split(Regex(",|，|\\s+|\\.|!|\"|:")).map {
                it.replace(Regex("^\\W+|\\W+$"), "")
            }.filter {
                it.length > 0 && !it.matches(Regex("^\\d.+"))
            }

            allWords.forEach {
                if (!(it in words)) {
                    words[it] = 0;
                }
                words[it] = words[it]!! + 1
            }
            docWordSum+=allWords.size
            progress.value = (docPerCount++ / docPerSum).toFloat()
        }
        val sortArray = words.filter {
            it.value > 1
        }.toList().sortedByDescending {
            it.second
        }

        sortedList.value = if (sortArray.size > 3000)
            sortArray.subList(0, 3000)
        else if(sortArray.size>0)
            sortArray
        else
            words.toList()
    }
    var runOneceInit = arrayMapOf(viewModes.listen to false,viewModes.stat to false,viewModes.read to false);
    fun initData(textToRead:List<String>) {
       if(runOneceInit[vMode]!!) return;
        runOneceInit[vMode] = true;
        tts.setSpeechRate(speechRate)
        tts.voice = selectedVoice ?: tts.defaultVoice
        when(vMode){
            viewModes.listen -> {
                if(sentencesQueue.isEmpty()){
                    textToRead.forEach{text->
                        val sentenceDelimiterRegex = Regex("[,.;?!，。；？！]")
                        val rows = sentenceDelimiterRegex.split(text)  //.filter { it.isNotBlank() }
                        sentencesQueue.addAll(rows)
                        j2dMap.add(sentencesQueue.size)
                    }
                }
            }
            viewModes.stat -> {
                if(sortedList.value.size==0) {
                    statRun(textToRead)
                }
            }
            else ->{

            }
        }
    }

    fun continueSpeakingFromIndex(index: Int) {
        currentSentenceIndex = index
        tts.stop()
        onChangePage()
        speakOut()
    }
    private fun onChangePage() {
        //清空上一个选中状态
        textColors[dIndex] = bColor
        //根据偏移位置定位段落id
        var i=dIndex
        if(j2dMap[i] > currentSentenceIndex) i=0
        while (j2dMap[++i] < currentSentenceIndex);
        dIndex = i

        //新段落颜色标记
        textColors[dIndex] = Color.Gray
    }


    private fun onSpeakDone() {
        if(currentSentenceIndex>=sentencesQueue.size) {
            isOpenSpeaking = false
            dIndex =0
            currentSentenceIndex =0
        }

        currentSentenceIndex++
        speakOut(1000)
    }
    fun speakOut(sleep:Long=0) { // numSentences is the number of sentences to read at a time
        if (!isInitialized || sentencesQueue.size==0)  return

        var sentencesToSpeak = sentencesQueue[currentSentenceIndex]
        //如果数据为空则继续查找
        while(sentencesToSpeak.isEmpty()) sentencesToSpeak = sentencesQueue[++currentSentenceIndex]

        //如果在当前段落中播放并直接返回
        if(currentSentenceIndex >= j2dMap[dIndex] && currentSentenceIndex < j2dMap[dIndex+1]) {
            tts.speak(sentencesToSpeak, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
            return
        }
        //段落停止判断
        if (!isOpenSpeaking)   return

        onChangePage()
        //发出段落第一个句子的读音
        Thread.sleep(sleep)
        tts.speak(sentencesToSpeak, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }
    fun loadTextFromUrl(url: String) {
        if(url.startsWith("content://")) {
            val uri = Uri.parse(url)
            val textData =  FileUtil.readTextFile(uri)
            textToRead = textData.split(Regex("[\\r\\n]+"))
            return
        }else if(url.matches(Regex("^\\d+$"))){
            Article.getContent(url.toInt()) { textData->
//                val tData = if(textData.length<500000) textData else textData.substring(0,500000)
                textToRead = textData.split(Regex("[\\r\\n]+"))
               // MainRun { ActivityRun.msg("article read ok perCount:"+textData.length) }
            }
            return
        }

        val client = OkHttpClient()
        fun String.convertANSIToUTF8(): String {
            val ansiBytes = this.toByteArray(charset("ISO-8859-1")) // 假设是ANSI编码
            return String(ansiBytes, charset("UTF-8"))
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    var textData = response.body?.string() ?: ""
                    textData = textData.convertANSIToUTF8()
                    withContext(Dispatchers.Main) {
//                        if(textData.length>500000) textData = textData.substring(0,500000)
                        textToRead = textData.split(Regex("[\\r\\n]+"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, maybe update the UI to show an error message
            }
        }
    }
    fun initializeTTS() {
        tts = TextToSpeech(ActivityRun.context, this)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts.setLanguage(Locale.US)

            // Load voices here
            val voices = tts.voices
            if (!voices.isNullOrEmpty()) {
                availableVoices.addAll(voices)
//                    selectedVoice = tts.voice ?: voices.first()
                selectedVoice = tts.defaultVoice
            }
//            // Initialize availableVoices with available TTS voices
//            availableVoices.addAll(tts.voices ?: emptySet())
//            // Default to the current voice or the first available one
//            selectedVoice = tts.defaultVoice

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String) {
                    onSpeakDone()
                }

                override fun onError(utteranceId: String?) {}
            })
        }
    }
}
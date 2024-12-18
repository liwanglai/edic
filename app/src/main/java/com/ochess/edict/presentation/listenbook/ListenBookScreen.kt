package com.ochess.edict.presentation.listenbook

import android.annotation.SuppressLint
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ochess.edict.R
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.data.model.Article
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ActivityRun.Companion.onBackPressed
import com.ochess.edict.util.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Locale
import java.util.UUID

@Composable
fun ListenBookScreen(words: List<String>) {
    val lbd = ListenBookData()
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
            initData()
        }
        LaunchedEffect(dIndex) {
            scrollState.animateScrollToItem(dIndex, -500)
        }


        bColor = MaterialTheme.colorScheme.background
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize() // Remove padding
                    .padding(5.dp)
            ) {
                Row {
                    var buttonTitle by remember { mutableStateOf("Speak") }
                    Button(onClick = {
                        isOpenSpeaking = !isOpenSpeaking
                        if (buttonTitle == "Speak") {
                            buttonTitle = "Speaking"
                            speakOut()
                        } else {
                            buttonTitle = "Speak"
                            tts.stop()
                        }
                    }) {
                        Text(mt(buttonTitle)) // Set text color to soft white
                    }
                    Column (modifier = Modifier.padding(10.dp,1.dp)){
                        Text(
                            mt("播放速度")+": ${
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
                            valueRange = 0.1f..2.0f, // Extend the range to 5
                            steps = 30
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
                Box {
                    scrollState = rememberLazyListState()
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize() // Remove padding

                    ) {
                        items(textToRead.size) {
                            textColors.add(bColor)
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
            }
        }
    }
}



class ListenBookData :TextToSpeech.OnInitListener {

     var isOpenSpeaking: Boolean = false
     var sampleFileUrl = NetConf.resRoot+"/books/test.txt"

     var tts: TextToSpeech = TextToSpeech(ActivityRun.context, this)
     val sentencesQueue = arrayListOf<String>()
     var textToRead by mutableStateOf(listOf<String>())

     var speechRate by mutableStateOf(1.0f) // Normal speed is 1.0
     var speechVolume by mutableStateOf(1.0f) // Normal speed is 1.0


     var voice: Voice? = null
     var selectedVoice by mutableStateOf(voice)
     val availableVoices = mutableStateListOf<Voice>()

     var currentSentenceIndex = 0 // Track the index of the current sentence
     var isInitialized by mutableStateOf(false) // Track if TTS is initialized

     var textColors by mutableStateOf(arrayListOf<Color>())
     var dIndex by mutableStateOf(0)
     var j2dMap = arrayListOf(0)
     var bColor: Color = Color.White
     lateinit var scrollState: LazyListState

    fun initData() {
        if(sentencesQueue.isEmpty()){
            textToRead.forEach{text->
                val sentenceDelimiterRegex = Regex("[,.;?!，。；？！]")
                val rows = sentenceDelimiterRegex.split(text)  //.filter { it.isNotBlank() }
                sentencesQueue.addAll(rows)
                j2dMap.add(sentencesQueue.size)
            }
            tts.setSpeechRate(speechRate)
            tts.voice = selectedVoice ?: tts.defaultVoice
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
        if (!isInitialized)  return

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
            textToRead = textData.split(Regex("[\\r\\n]"))
            return
        }else if(url.matches(Regex("^\\d+$"))){
            Article.getContent(url.toInt()) { textData->
                textToRead = textData.split(Regex("[\\r\\n]"))
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
                        textToRead = textData.split(Regex("[\\r\\n]"))
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
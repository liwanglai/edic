package com.ochess.edict.presentation.home

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.NetConf
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.presentation.main.components.ProgressDialog
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.FileUtil
import com.ochess.edict.util.media.Audio
import java.io.File
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TTSListener(context: Context, private var onSpeechCompleted: () -> Unit) : DefaultLifecycleObserver {
    //读音改串行通过play方法
    private  var countDownLatch: CountDownLatch? = null
    private var onInit = false
    private var textToSpeechEngine: TextToSpeech = TextToSpeech(context, { status ->
        if (status == TextToSpeech.SUCCESS) {
            onInit = true
        }else{
            Toast.makeText(context, "TextToSpeech.status "+status, Toast.LENGTH_SHORT).show()
        }
    } ,"com.google.android.tts")
    var onDoneOne:()->Unit = {}

    fun playEnd(){
        onSpeechCompleted()
        if(onDoneOne != {}) {
            onDoneOne()
            onDoneOne = {}
        }
        if(countDownLatch!=null) {
            countDownLatch?.countDown()
        }
    }
    init {
        textToSpeechEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
            override fun onStart(utteranceId: String?) {

            }

            override fun onDone(utteranceId: String?) {
                playEnd()
            }

            override fun onError(utteranceId: String?) {
                Log.d(TAG, "onError: $utteranceId")
                onSpeechCompleted()
            }

        })
    }
    fun getSpeech(): TextToSpeech {
        return textToSpeechEngine
    }
    fun setLocale(lang: Locale){
        textToSpeechEngine.setLanguage(lang)
    }
    fun setOnCompleted(onDone:()->Unit){
        onSpeechCompleted = onDone
    }


    fun stop() {
        textToSpeechEngine.stop()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        textToSpeechEngine.stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        textToSpeechEngine.shutdown()
    }


    fun speak(text: String,onOver: () -> Unit) {
        if (onInit) {
            onDoneOne=onOver
            speak(text)
            //textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
        }
    }
    fun speak(text: String) {
        val type = if(textToSpeechEngine.language!=null && textToSpeechEngine.language.toString().matches(Regex(".+_US.*")))  "us/" else ""
        val file = FileUtil.rootDir() + "/mp3/"+type+text+".mp3"
        if(FileUtil.exists(file)) {
            Audio.play(file){}
            playEnd()
            return
        }
        if (onInit) {
            textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts0")
        }
    }
    fun play(word: String,lang:Locale=Locale.ENGLISH) {
        countDownLatch = CountDownLatch(1)
        setLocale(lang)
        speak(word)

        //等待返回结果并返回
        try {
            countDownLatch?.await(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun getInstance(context: Context): TTSListener {
            if(instance ==null) {
                instance = TTSListener(context){}
            }
            return instance as TTSListener
        }

        private var instance:TTSListener?=null
        private fun wordAddMp3(text:String, file:String): Boolean {

            //生成失败尝试
            var tn =3
            while(tn-->0) {
                val size = GlobalVal.tts.getSpeech()
                    .synthesizeToFile(text, HashMap<String, String>(), file)
                var en = 50
                while(en-- >0) {
                    Thread.sleep(10)
                    if (size > 0 || File(file).length() > 0L) {
                        Log.d(TAG, "wordAddMp3: " + file)
                        return true
                    }
                }
            }
            val rt = File(file).length() > 0L
            if(!rt){
                File(file).delete()
            }


            return rt
        }
        fun imgsCreate(word:String){
            val url = NetConf.imgs
            val file = "$word.jpg"
            val path = PathConf.imgs
            if(!FileUtil.exists(path)) {
                FileUtil.mkdir(path)
                val am = ActivityRun.context.resources.assets
                val baseWords = am.list("imgs")
                for(f in baseWords!!) {
                    val ins = am.open("imgs/"+f)
                    val bytes = ByteArray(ins.available())
                    ins.read(bytes)
                    FileUtil.put(path + f,bytes, false)
                    ins.close()
                }
            }
            val exists = FileUtil.exists(path+file)
            if(!exists) {
                FileUtil.get(url + file) {
                    FileUtil.put(it, path + file)
                }
            }
        }

        fun mp3Create(words: List<String>?,function: (okCount:Int) -> Unit){
            val size = words!!.size
            ProgressDialog.show()
            val dir = FileUtil.rootDir() + "/mp3/"
            FileUtil.mkdir(dir)
            Thread {
                var i = 0
                var okSize = 0
                for (word in words!!) {
                    imgsCreate(word)
                }
                GlobalVal.tts.setLocale(Locale.UK)
                //循环创建mp3
                for (word in words!!) {
                    val file = dir + word + ".mp3"
                    if (!File(file).exists()) {
                        if (wordAddMp3(word, file)) {
                            okSize++
                        }
                        i += 1
                        ActivityRun.runOnUiThread {
                            ProgressDialog.Progress(i, size)
                            Log.d(TAG, "mp3CreateIndex: " + i.toString())
                        }
                    }
                }
                ProgressDialog.close()
//                ActivityRun.runOnUiThread {
//                    function(okSize)
//                }

                val dir2 = FileUtil.rootDir() + "/mp3/us/"
                FileUtil.mkdir(dir2)
                GlobalVal.tts.setLocale(Locale.US)
                for (word in words!!) {
                    val file = dir2 + word + ".mp3"
                    if (!File(file).exists()) {
                        wordAddMp3(word, file)
                        //imgsCreate(word)
                    }
                }

            }.start()
            ActivityRun.runOnUiThread {
                function(1)
            }
        }

    }
}
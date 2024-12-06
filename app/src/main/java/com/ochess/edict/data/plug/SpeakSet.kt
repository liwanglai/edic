package com.ochess.edict.data.plug

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.collection.arrayMapOf
import com.ochess.edict.data.config.SpeakConf
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.view.MPopMenu
import java.util.Locale

class SpeakSet(val textToSpeechEngine: TextToSpeech, val spConf: SpeakConf) {
    val rateMap = arrayMapOf(
        1 to 0.1f,
        2 to 0.5f,
        3 to 1f,
        4 to 2f,
        5 to 5f
    )
    val volumeMap = arrayMapOf(
        "男声" to 0.5f,
        "女声" to 1f,
        "小孩" to 2f,
    )
    fun update() {
        var voices = textToSpeechEngine.voices
        var nowVoice = textToSpeechEngine.voice
        textToSpeechEngine.setLanguage(spConf.language)
        if(voices != null && voices.size>0) {
            for (voice in voices) {
                // 根据语言选择声音，这里选择了美国英语
                if (voice.name.equals("en")) {
                    nowVoice = voice
                    break
                }
            }
        }
        textToSpeechEngine.setSpeechRate(rateMap[spConf.speechRate]!!)
        textToSpeechEngine.setVoice(nowVoice)
        textToSpeechEngine.setPitch(volumeMap[spConf.volume]!!)
    }

    companion object {
        //菜单内容更新
        fun upMenu(mpm: MPopMenu, spConf: SpeakConf) {
            var menu = MPopMenu.speakItems.clone() as ArrayList<MPopMenu.dataClass>
            spConf.forEach {
                menu.add(it)
            }
            mpm.items =menu
        }
    }
}
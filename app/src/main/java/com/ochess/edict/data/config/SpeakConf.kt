package com.ochess.edict.data.config

import androidx.collection.arrayMapOf
import com.fasterxml.jackson.annotation.JsonProperty
import com.ochess.edict.data.UserStatus
import com.ochess.edict.view.MPopMenu
import java.util.Locale

/**
 * 发音配置
 * getByUserConfig方法读取配置
 * save方法是保存配置
 * foreach方法是给出菜单列表
 *      用到了json数据的存储与恢复
 *      实现了[]方法的重载用于设置和读取
 */
data class SpeakConf(
    @JsonProperty("language")
    var language: Locale=Locale.US,
    @JsonProperty("speechRate")
    var speechRate: Int = 3,
    @JsonProperty("repeatNum")
    var repeatNum: Int = 50,
    @JsonProperty("volume")
    var volume: String = "女声"
) {
//    var language: Locale = Locale.UK
//    var speechRate = 1f
//    var repeatNum = 50
//    var speechStyle = "女生"
//    var volume = 4f
//
    operator fun <T> get(k:String): T {
        var f= this.javaClass.getField(k)
        return f.get(this) as T
    }
    operator fun set(k:String,v:Any){
        when(k){
            "language" -> // "语种",
                language = v as Locale
            "speechRate" -> //to "播放速度",
                speechRate = v as Int
            "repeatNum" -> //to "重复次数",
                repeatNum =v as Int
            "volume" -> //to "音频率"
                volume =v as String
        }
//        var f= this.javaClass.getField(k)
//        f.set(this,v)
    }
    fun save(k:String="",v:Any=0){
        if(k.length>0){
            this[k]=v
        }
        UserStatus().setData(this.javaClass.name,this)
    }

    fun getName(key:String ): String {
        var rt = ""
        when(key){
            "language" -> // "语种",
                rt = lanageMap[language].toString()
            "speechRate" -> //to "播放速度",
                rt = speechRate.toString()
            "repeatNum" -> //to "重复次数",
                rt = repeatNum.toString()
            "volume" -> //to "音频率"
                rt = volume
        }
        return rt
    }
    fun forEach(oneDo: (item: MPopMenu.dataClass) -> Unit) {

        keyMap.forEach{
            var title = it.value
            val name = it.key
            title+="("+getName(it.key)+")"
            val value =options[it.key]
            var child = arrayListOf<MPopMenu.dataClass>()

            value?.forEach {
                var mdc = MPopMenu.dataClass(it.toString(),name,it)
                if(name.equals("language")){
                    var i:Int = lanageMap.values.indexOf(it)
                    mdc.value = lanageMap.keyAt(i)
                }
                child.add(mdc)
            }
            oneDo(MPopMenu.dataClass(title,child=child))
        }
    }
    companion object {
        var  current:SpeakConf  =SpeakConf()
        fun getByUserConfig() : SpeakConf{
            var rt = UserStatus().getData(SpeakConf::class.java.name,SpeakConf::class.java)
            if(rt!=null) {
                current = rt as SpeakConf
            }
            return current
        }
        val lanageMap = arrayMapOf(
            Locale.US to "美",
            Locale.UK to "英"
        )
        val keyMap = linkedMapOf(
            "repeatNum" to "重复次数",
            "language" to "语种",
            "speechRate" to "播放速度",
            "volume" to "朗读人"
        )
        val options = arrayMapOf(
            "repeatNum" to arrayOf(5, 10, 30, 50, 100),
            "speechRate" to arrayOf(1, 2, 3, 4, 5),
            "language" to lanageMap.values.toTypedArray(),
            "volume" to arrayOf("男声","女声","小孩")
        )
        init {
        }
    }


}
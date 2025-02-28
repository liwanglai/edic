package com.ochess.edict.util

import com.ochess.edict.presentation.listenbook.LoadJsonItem
import com.ochess.edict.presentation.main.extend.MainRun
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.util.NetDicUtil.Companion.doc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException


interface NetDicUtil {

    abstract fun getMeanings(): String?
    abstract fun getCh(): String?
    //网络获取 返回的内容会赋值到document中
    fun get(url: String) {
        val client = OkHttpClient()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    document = response.body?.string() ?: ""
                    doc = Jsoup.parse(document)
                    onGetDoc()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                MainRun {
                    ActivityRun.msg("NetGetErr:" + e.message.toString())
                }
            }
        }
    }
    companion object {
        lateinit var doc :Document;
        var document=""
        var onGetDoc:NetDicUtil.()->Unit = {}
        fun youdaoGet(word: String,callbak:NetDicUtil.()->Unit): YouDaoDic {
            onGetDoc = callbak
            return YouDaoDic(word)
        }
        fun bingGet(word: String,callbak:NetDicUtil.()->Unit): NetDicUtil {
            onGetDoc = callbak
            return BingDic(word)
        }
    }

}

class BingDic(word: String) :NetDicUtil{
    val url = "https://youdao.com/result?word=%s&lang=en"

    init {
        get(String.format(url,word))
    }
    override fun getMeanings(): String? {
        //音标
        val symbol = doc.select(".hd_p1_1").map {
            it.text()
        }.joinToString(",")
        //时态
        val tense = doc.select(".hd_if").text()
//        tense = tense.split("[：\s]+")
//        tense = json.dumps(listToDict(tense),ensure_ascii=False) if len(tense)>1 else ""
//
        //短语搭配
        val phrase = doc.select("#colid").text()
        //反义词
        val antonym = doc.select("#antoid").text()
        //同义词
        val synonyms = doc.select("#synoid").text()
        //详情
        val meanings = arrayListOf<String>()
        var childAll = doc.select("#pos_0 >div")
        for (em in childAll) {
            val cname = em.attr("class")[0]
            val spans = em.select("span")
            if (cname.equals("se_lis")) {
                if (spans.size < 2) continue
                val en = spans[0].text()
                val cn = spans[1].text()
                meanings.add(en + ":" + cn)
            }
        }

        //例句
        val example = arrayListOf<String>()
        childAll = doc.select("#sentenceSeg .se_li1")
        for (exm in childAll){
            val en = exm.select(".sen_en").text()
            val cn = exm.select(".sen_cn").text()
            example.add(en + ":" + cn)
        }

        return "";
    }

    override fun getCh(): String? {
        val en = doc.select(".def").text()
        return en;
    }

}
class YouDaoDic(word: String) :NetDicUtil{
    val url = "https://youdao.com/result?word=%s&lang=en"

    init {
       get(String.format(url, word))
    }

    override fun getMeanings(): String? {
        val rt = arrayListOf<String>()
        val lis:Elements = doc.select(".blng_sents_part .trans-container li")
        for(item in lis){
            val a = item.select(".word-exp").map{it.text() }
            rt.add("{example:\"${a[0]}\",example_ch:\"${a[1]}\"}")
        }
        return "["+rt.joinToString(",")+"]";
    }

    override fun getCh(): String? {
        val lis:Elements= doc.select(".basic li")
        return lis.map{ it.text()}.joinToString(",")
    }

}
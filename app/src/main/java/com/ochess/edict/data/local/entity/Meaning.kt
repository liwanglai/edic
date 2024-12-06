package com.ochess.edict.data.local.entity

import android.util.ArrayMap
import com.google.gson.annotations.SerializedName

data class Meaning(
    /**
     * 意思
     */
    @SerializedName("def")
    val def: String?,

    /**
     * 例句
     */
    @SerializedName("example")
    val example: String?,

    /**
     *演讲部分 词语类型
     */
    @SerializedName("speech_part")
    val speechPart: String,

    /**
     * 同义词
     */
    @SerializedName("synonyms")
    val synonyms: List<String>?,

    /**
     * 标签
     */
    @SerializedName("labels")
    val labels: List<Label>?,


    val example_ch: String?,

    val def_ch: String?,

    /**
     * 反义词
     */
    val antonym: List<String>?,

    /**
     * 衍生词
     */
    val derived: List<String>?,

    /**
     * 短语搭配
     */
    val phrase: List<String>?,

    /**
     * 时态
     */
    val tense: ArrayMap<String, String>?,

    /**
     * 音标   首词条 英,美
     */
    val symbol: List<String>?,
)
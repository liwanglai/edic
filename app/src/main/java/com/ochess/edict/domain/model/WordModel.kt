package com.ochess.edict.domain.model

import com.ochess.edict.data.Db
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.data.local.entity.Meaning
import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.data.model.Query
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.util.FileUtil

data class WordModel(
    val meanings: List<Meaning>?,
    val word: String,
    val wordsetId: String,
    val level: Int,
    var status: Int = 0,  // 初始、认识、模糊、忘记
    var ch: String?,
    val date:String ="" //时间如果需要 格式为yyyy-MM-dd HH:mm:ss
) {
    var pic=""
    var isStudyed:Boolean = false
    fun toBookmarkEntity(): BookmarkEntity {
        return BookmarkEntity(meanings, word, wordsetId,
            ch=ch?:"", intime = System.currentTimeMillis()
        )
    }

    fun toHistoryEntity(): HistoryEntity {
        return HistoryEntity(meanings, word, wordsetId, level, status,
            System.currentTimeMillis(), ch)
    }

    fun toDictionaryEntity(): DictionaryEntity {
        return DictionaryEntity("", word, wordsetId, ch,level)
    }

    fun hasPic(): Boolean {
        var imgFile = PathConf.imgs+word+".jpg"
        pic = imgFile
        return FileUtil.exists(imgFile)
    }

    companion object {
        const val STATUS_KNOW = 1
        const val STATUS_VAGUE = 2
        const val STATUS_FORGET = 3
        const val STATUS_NEW = 0
    }
}
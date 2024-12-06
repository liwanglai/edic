package com.ochess.edict.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavHostController
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.BookmarkViewModel
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.util.ActivityRun

object GlobalVal {
    var pid = -100
    /**
     * 历史页的分组信息表
     */
    val historyGroup:LinkedHashMap<String,List<String>> = linkedMapOf()
    var wordModelList: List<WordModel> = listOf<WordModel>()
    lateinit var clipboardManager: ClipboardManager
    lateinit var bookmarkViewModel: BookmarkViewModel
    lateinit var nav: NavHostController
    lateinit var wordViewModel: WordModelViewModel
    val floatOffet = mutableStateOf(IntOffset(0, 0))
    var floatText = mutableStateOf("这里显示中文意思")
    val floatSize = mutableStateOf(IntSize.Zero)

    var isSearchVisible = mutableStateOf(false)

//    lateinit var tts: TTSListener
    val tts: TTSListener = TTSListener(ActivityRun.context()){}
}
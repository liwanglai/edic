package com.ochess.edict.presentation.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.glide.rememberGlidePainter
import com.ochess.edict.data.Db
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.local.entity.DictionaryEntity
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.data.model.Category
import com.ochess.edict.data.model.Query
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.domain.repository.DictionaryBaseRepository
import com.ochess.edict.domain.repository.WordBaseRepository
import com.ochess.edict.presentation.history.BookHistroy
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.home.components.OverJump
import com.ochess.edict.presentation.main.components.InfoDialog
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WordModelViewModel @Inject constructor(
    private val wordRepo: WordBaseRepository,
    private val dictRepository: DictionaryBaseRepository
) : ViewModel() {

    var wordState = MutableStateFlow(WordState())
        private set

    var suggestions = MutableStateFlow(emptyList<String>())
        private set

    var detailState = MutableStateFlow(false)

    var selectLevel = MutableStateFlow(-1)
        private set
    private var workIndex = -1

    // 缓存当前字段单词和WordSetId
    private val currentDictionarySub = MutableStateFlow(DictionarySubEntity.empty())
    val currentDictionary = MutableStateFlow(DictionaryEntity(null, "", "", "", -1))

    private var searchJob: Job? = null
    private var prefixMatchJob: Job? = null

    fun initByLevel(level: Int) {
        if (level > -1) {
            viewModelScope.launch(IO) {
                Log.d(TAG, "initByLevel() level = $level")
                dictRepository.getWordSetInCacheByLevel(level).firstOrNull()
            }
        }
        selectLevel.value = level
    }

    fun changeLevelAndGetNewRandomDictionary(level: Int,index:Int=-1) {
        if (level > -1) {
            viewModelScope.launch(IO) {
                Log.d(TAG, "changeLevelAndGetNewRandomDictionary() level = $level")
                dictRepository.getWordSetInCacheByLevel(level).firstOrNull()
                setWordState(index)
                selectLevel.value = level
            }
        }
    }

    fun setWord(it: DictionarySubEntity) {
        currentDictionarySub.value = it
        wordState.value = WordState(it.toWordModel())
        detailState.value = false
    }
    private fun setWordState(i:Int = -1): WordState {
            var index = i
            val randIndex = dictRepository.randomCacheSubEntityIdx()
            if(index == -1 || randIndex == -1) {
                index = randIndex
                if (index == -1) return wordState.value
            }else{
                index = dictRepository.getWorkIndex(0-index)
                //搜索之前 查询了一次从列表里删除了
                if(index == -1) index = randIndex
            }

            val size = dictRepository.size()
            if(index>size) return wordState.value
            Log.d(TAG, "Index = $i $randIndex $index/$size")
            workIndex = dictRepository.getWorkIndex(index)
            dictRepository.getCacheSubEntity(index)?.let {
                currentDictionarySub.value = it
                wordState.value = WordState(it.toWordModel())
                detailState.value = false
            } ?: DictionarySubEntity.empty()
        return wordState.value
    }


    fun searchByText() {
        searchJob?.cancel()
        val current = currentDictionarySub.value.word
        searchJob = viewModelScope.launch(IO) {
            val result = dictRepository.search(current).firstOrNull()?.first()
                ?: DictionaryEntity.emptyDictionary()
            currentDictionary.value = result
            wordState.value = WordState(result.toWordModel())
            if(result.word.length>0) {
                detailState.value = true
            }
        }
    }

    fun searchByWordSetId() {
        searchJob?.cancel()
        val wordSetId = currentDictionarySub.value.wordsetId
        searchJob = viewModelScope.launch(IO) {
            val result = dictRepository.search(wordSetId).firstOrNull()?.first()
            currentDictionary.value = result ?: DictionaryEntity.emptyDictionary()
        }
    }

    fun nextDictionaryWord() {
        viewModelScope.launch(IO) {
            val index = dictRepository.randomCacheSubEntityIdx()
            Log.d(TAG, "Index = $index")
            dictRepository.getCacheSubEntity(index)?.let {
                currentDictionarySub.value = it
                wordState.value = WordState(it.toWordModel())
                detailState.value = false
            } ?: DictionarySubEntity.empty()
        }
    }

    fun prefixMatcher(query: String,searchWords:Boolean=true,onMatch:(word:String)->Boolean) {
        clearSuggestions()
        prefixMatchJob?.cancel()
        prefixMatchJob = viewModelScope.launch(IO) {
            if (!searchWords || query.startsWith(":")) {
                val queryKey = if(searchWords)  query.substring(1) else query;
                val sugs = arrayListOf<String>()
                val aQuery = Article.self.query.like("name",queryKey).build()
                val cQuery = Category.self.query.like("name",queryKey).build()
                val lQuery = Query(Db.dictionary,"levelTable").like("name",queryKey).build()
                val a = Db.user.article.select(aQuery).map { "article.${it.id}:${it.name}" }
                val c = Db.user.category.select(cQuery).map { "category.${it.id}:${it.name}" }
                val l = Db.dictionary.levelDao.select(lQuery).map { "level.${it.id}:${it.name}" }
                sugs.addAll(a); sugs.addAll(c); sugs.addAll(l)
                suggestions.value = sugs
            } else  if(query.matches(Regex("[\u4E00-\u9FA5]+"))) {

                dictRepository.searchByCh(query).collect { matches ->
                    suggestions.value = matches.map{ it.word+":"+it.ch}
                }
            }else{
                dictRepository.prefixMatch(query).collect { matches ->
                    matches.let { match ->
                        val sublist = match.map { it.word }
                        suggestions.value = sublist

                        match.forEach {
                            if (it.word == query) {
                                val setSug = onMatch(query)
                                if(setSug) {
                                    val moreSug = arrayListOf<String>()
                                    moreSug.addAll(sublist)
                                    moreSug.addAll(
                                        Article.grep(query).map { "article.${it.id}:${it.name}" })
                                    val queryObj =
                                        Query(Db.user, "historyTable").like("word", query).build()
                                    moreSug.addAll(
                                        Db.user.wordModelDao.getHistoryList(queryObj).map {
                                            val date =
                                                SimpleDateFormat("yyyy-MM-dd").format(Date(it.time))
                                            "history:${date}"
                                        })
//                                if(it.level>0) {
//                                    val levelEntity = Db.dictionary.levelDao.queryName(it.level)
//                                    moreSug.add("level.${it.level}:" + levelEntity.name)
//                                }
                                    suggestions.value = moreSug
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun searcher(query: String, isWordClick: Boolean = false) {
        if (query.isBlank()) {
            clearSuggestions()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch(IO) {
            if(query.indexOf(":") > 0){
                val v = query.split(Regex("[:\\.]+"))
                val key =v.first()
                val id = if(v[1].length<10) v[1].toInt() else 0
                val value = v.last()
                when(key){
                    "level" -> {
//                        changeLevelAndGetNewRandomDictionary(id)
                        NavScreen.openHome(PAGE_FROM_LEVEL,0,id)
                    }
                    "history" ->{
                        HistoryViewModel.searchDay(value)
                        NavScreen.openHome(PAGE_FROM_HISTORY)
                    }
                    "article" ->{
                        BookConf.setBook(id)
                        NavScreen.openHome(PAGE_FROM_BOOKMARK)
                    }
                    "category" ->{
                        Category.getArticles(id, true) {
                            val rt = arrayListOf<String>()
                            it.forEach {
                                rt.addAll(it.findWords())
                            }
                            GlobalVal.bookmarkViewModel.upListByWords(rt) {
                                NavScreen.openHome(PAGE_FROM_BOOKMARK)
                            }
                        }
                    }
                }
                return@launch
            }
            val result = dictRepository.search(query).firstOrNull()?.first()
            result?.let {
                wordState.value = WordState(it.toWordModel())
                currentDictionary.value = DictionaryEntity(it.meanings,it.word,it.wordsetId,it.ch,it.level)
                currentDictionarySub.value = DictionarySubEntity(word = it.word, wordsetId = it.wordsetId, level = it.level)
                BookConf.instance.next(wordState.value.wordModel!!)
                if (isWordClick) insertHistory(it.toWordModel())
                //当前视图是单词列表做一步返回
                if(viewMode.equals(MenuConf.mode.chapterPage)){
                    HomeEvents.onback(true)
                }
            }
        }
    }

    fun clearSuggestions() {
        suggestions.value = emptyList()
    }

    fun insertBookmark(wordModel: WordModel) {
        viewModelScope.launch(IO) {
            wordRepo.insertBookmark(wordModel.toBookmarkEntity())
        }
    }

    fun insertHistory(wordModel: WordModel) {
        viewModelScope.launch(IO) {
            try {
                wordRepo.insertHistory(wordModel.toHistoryEntity())
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * 显示下一个
     * uOrder 不要排序无序的
     * detailShow 显示详情
     */
    fun showNext(uOrder: Boolean,detailShow:Boolean = false) {
        if(dictRepository.size()>0) {
            var index = dictRepository.randomCacheSubEntityIdx()
            if(index<0) {
                //ActivityRun.msg("已经是最后一个了")
                InfoDialog.show{
                    OverJump()
                }
                return
            }
            if(!uOrder) index =0
            //workIndex = dictRepository.getWorkIndex(index)
            dictRepository.getCacheSubEntity(index)?.let {
                currentDictionarySub.value = it
                wordState.value = WordState(it.toWordModel())
                BookConf.instance.index++
                BookConf.instance.next(wordState.value.wordModel!!)
            } ?: DictionarySubEntity.empty()

            detailState.value = detailShow
        }else{
            InfoDialog.show{
                OverJump()
            }
        }
    }
    fun cacheSub():List<WordModel>{
        return dictRepository.getWords()
    }

    fun upList(wordModelList: List<WordModel>) {
        var wordList = if(BookConf.instance.index+1 == wordModelList.size || wordModelList.size==0) arrayListOf<WordModel>() else wordModelList.subList(BookConf.instance.index+1,wordModelList.size)
        dictRepository.setWords(wordList)
        //setWordState(0)
    }


    companion object {
        const val TAG = "WordModelViewModel"
    }
}
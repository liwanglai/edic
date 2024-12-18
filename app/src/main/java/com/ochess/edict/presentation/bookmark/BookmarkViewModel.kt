package com.ochess.edict.presentation.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.Db
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.repository.WordRepository
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.history.BookHistroy
import com.ochess.edict.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val wordRepo: WordRepository): ViewModel() {
    var nowBook: BookConf = BookConf()
    var bookmarks = MutableStateFlow(listOf<WordModel>())
        private set

    fun deleteBookmark(wordModel: WordModel) {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepo.deleteBookmark(wordModel.toBookmarkEntity())
        }
    }

    init {
        if(bookmarks.value.size==0) {
//            viewModelScope.launch(Dispatchers.IO) {
//                wordRepo.getAllBookmark().collect { result ->
//                    bookmarks.value = result.map { it.toWordModel() }
//                }
//            }
        }
    }


    fun getAll(onOver:(a:List<WordModel>)->Unit={}): MutableStateFlow<ArrayList<WordModel>> {
        var rt = MutableStateFlow(arrayListOf<WordModel>())
        viewModelScope.launch(Dispatchers.IO) {
            Db.user.wordModelDao.getBookmark500().collect { result ->
                bookmarks.value = result.map { it.toWordModel() }
                if(bookmarks.value.size>0) {
                    rt.value = bookmarks.value as ArrayList<WordModel>
                    onOver(rt.value)
                }
            }
        }

        return rt
    }

    fun upListByWords(words: ArrayList<String>,function: (size:Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarks.value= Db.dictionary.dictionaryDao.search(words).map{
                it.toWordModel()
            }
            
            function(bookmarks.value.size)
        }
    }

    fun openBook() {
        //if(bookmarks.value.size>0) return
        BookConf.instance.initArticle()
        val countDownLatch = CountDownLatch(1)
        Thread{
            var n = 140
            while(n-- > 0) {
                if (BookConf.words.size > 0) countDownLatch.countDown()
                Thread.sleep(10);
            }
        }.start()
        countDownLatch.await(3000, TimeUnit.MILLISECONDS)
        bookmarks.value = BookConf.words
    }

    fun changeChapter(k: String) {
        GlobalVal.wordModelList = listOf<WordModel>()
        BookConf.instance.save(k)
        val countDownLatch = CountDownLatch(1)
        Thread{
            var n = 140
            while(n-- > 0) {
                if (GlobalVal.wordModelList.size > 0) countDownLatch.countDown()
                Thread.sleep(100);
            }
        }.start()
        countDownLatch.await(3500, TimeUnit.MILLISECONDS)
        GlobalVal.wordModelList
    }

    fun selectWord(name: String) {
        var index=bookmarks.value.map{it.word}.indexOf(name)
        val word = bookmarks.value[index]
        GlobalVal.wordViewModel.setWord(
            DictionarySubEntity(
                word.wordsetId,
                word.word,
                word.level
            )
        )
    }
}
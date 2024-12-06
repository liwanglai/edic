package com.ochess.edict.data.model

import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.Db
import com.ochess.edict.domain.model.WordModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class Word  @Inject constructor() : Model() {
    companion object {
        val dao = Db.dictionary.dictionaryDao
        val self = Word()
        fun find(word: String): WordModel? {
            return  self.waitLaunch({
                dao.find(word)
            })
        }

        fun gets(extword: List<String>) :List<WordModel>{
            return self.waitLaunch({
                 dao.search(extword).map { it.toWordModel() }
            })
        }
    }

    fun <T>waitLaunch(run: suspend CoroutineScope.() -> T?, ws:Long = 500) : T{
        val countDownLatch = CountDownLatch(1)
        var rt:T? = null
        viewModelScope.launch(Dispatchers.IO) {
            rt = run()
            countDownLatch.countDown()
        }
        countDownLatch.await(ws, TimeUnit.MILLISECONDS)
        return  rt as T
    }
}
package com.ochess.edict.data.model

import androidx.collection.arrayMapOf
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.bookmark.data.VirtualCommonItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TestHistory  @Inject constructor() : Model() {
    var historys = MutableStateFlow(listOf<TestEntity>())
        private set

    fun select() {
        viewModelScope.launch(Dispatchers.IO) {
            historys.value = dao.select(self.query.build())
        }
    }
    private fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
    companion object {
        val dao = Db.user.test
        val self = TestHistory()

        fun delete(id: Int) {
            dao.delete(id)
        }

        fun update(entity: TestEntity) {
            dao.update(entity)
        }
    }
}
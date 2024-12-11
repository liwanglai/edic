package com.ochess.edict.data.model

import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.Db
import com.ochess.edict.data.local.entity.TestEntity
import com.ochess.edict.util.TimeStampScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestHistory  @Inject constructor() : Model() {
    var historys = MutableStateFlow(listOf<TestEntity>())
        private set

    fun select(key: String="", date: TimeStampScope?=null) {
        val q = self.query
        if(key.length>0) {
            q.like("name",key)
        }
        if(date!=null) {
            q.whereBetween("intime",date.start,date.end)
        }
        viewModelScope.launch(Dispatchers.IO) {
            historys.value = dao.select(q.build())
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
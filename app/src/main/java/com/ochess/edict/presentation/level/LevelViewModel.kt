package com.ochess.edict.presentation.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.local.LevelDao
import com.ochess.edict.data.local.entity.LevelEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(private val levelDao: LevelDao) : ViewModel() {

    val levelList = MutableStateFlow<List<LevelEntity>>(mutableListOf())

    fun getAllLevel() {
        viewModelScope.launch (Dispatchers.IO){
            levelList.value = levelDao.queryAllLevel()
        }
    }

    fun getLevel(le: Int, over:(lName:String)->Unit) {
        var rt: String
        viewModelScope.launch (Dispatchers.IO){
            val lEntity = levelDao.queryName(le)
            if(lEntity!=null && lEntity.level!! >0) {
                rt = lEntity.name?:""
                over(rt)
            }
        }
    }
}
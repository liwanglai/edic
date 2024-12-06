package com.ochess.edict.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.local.entity.DictionarySubEntity
import com.ochess.edict.data.local.entity.LevelEntity
import com.ochess.edict.domain.repository.DictionaryBaseRepository
import com.ochess.edict.domain.repository.WordBaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val wordRepo: WordBaseRepository,
    private val dictRepository: DictionaryBaseRepository
) : ViewModel() {

    val level = MutableLiveData<List<LevelEntity>>()
    private val dictionarySub = MutableLiveData<List<DictionarySubEntity>>()
    val dictionarySubWithoutHistory = MutableLiveData<DictionarySubEntity>()

    fun getAllLevel() {
        viewModelScope.launch(Dispatchers.IO) {
            dictRepository.getAllLevel().firstOrNull()?.let {
                level.postValue(it)
            } ?: level.postValue(arrayListOf())
        }
    }

    fun getDictionarySubByLevel(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dictRepository.getWordSetInCacheByLevel(level).firstOrNull()?.let {
                dictionarySub.postValue(it)
            } ?: dictionarySub.postValue(arrayListOf())
        }
    }

    fun getDistDictionaryWithoutHistory(level: Int) {

    }
}
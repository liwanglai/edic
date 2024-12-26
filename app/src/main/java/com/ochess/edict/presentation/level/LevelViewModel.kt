package com.ochess.edict.presentation.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochess.edict.data.local.LevelDao
import com.ochess.edict.data.local.entity.LevelEntity
import com.ochess.edict.data.model.Article
import com.ochess.edict.domain.model.WordModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(private val levelDao: LevelDao) : ViewModel() {

    companion object{
        fun getTopInfo(rt:String): String {
            return when(rt){
                "Top0" -> "(16，33%，1/3)"
                "Top1" -> "(62，11%，1/10,:78,44%)"
                "Top2" -> "(95，6%，1/17, :173,50%)"
                "Top3" -> "(228，7%，1/14,:401,57%)"
                "Top4" -> "(231，4.5%，1/22,:632,62%)"
                "Top5" -> "(597，7%，1/13,:1229,69%)"
                "Top6" -> "(1251，8%，1/12,:2480,77%)"
                "Top7" -> "(1911，6%，1/16,:4391,83%)"
                else -> ""
            }
        }
    }
    val levelList = MutableStateFlow<List<LevelEntity>>(mutableListOf())

    fun getAllLevel() {
        viewModelScope.launch (Dispatchers.IO){
            levelList.value = levelDao.queryAllLevel()
        }
    }

    fun getLevel(wm: WordModel, over:(lName:String)->Unit) {
        var rt: String
        val le = wm.level
        val arts = Article.grep(wm.word){
            this.like("name","Top%")
        }
        if(arts.size>0) {
            rt = arts.first().name.substring(0, 4)
            if (rt.length > 0) {
                rt += getTopInfo(rt)
                over(rt)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val lEntity = levelDao.queryName(le)
            if (lEntity != null && lEntity.level!! > 0) {
                rt += lEntity.name ?: ""
                over(rt)
            }
        }

    }
}
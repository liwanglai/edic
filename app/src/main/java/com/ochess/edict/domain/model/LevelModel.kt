package com.ochess.edict.domain.model

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ochess.edict.data.local.entity.Meaning
import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.presentation.level.LevelViewModel

data class LevelModel(
    val id: Int,
    val name: String?, // 等级名字全称
    val level: Int? // 等级值, 大于0
) {

}
package com.ochess.edict.presentation.home

import com.ochess.edict.domain.model.WordModel

data class WordState(
    val wordModel: WordModel? = null,
    var isLoading: Boolean = false,
    var isContained: Boolean = false,
    var hasDetail: Boolean = false,
    var level: Int? = null,
    var fromPage: Int? = null
)
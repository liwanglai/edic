package com.ochess.edict.util

import com.ochess.edict.domain.model.WordModel

class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isValidWord = false
    var wordModel: WordModel? = null
}
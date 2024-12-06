package com.ochess.edict.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ochess.edict.data.local.ArticleDao
import com.ochess.edict.data.local.CategoryDao
import com.ochess.edict.data.local.TestDao
import com.ochess.edict.data.local.WordModelDao
import com.ochess.edict.data.local.converters.LabelConverter
import com.ochess.edict.data.local.converters.MeaningConverter
import com.ochess.edict.data.local.converters.SynonymConverter
import com.ochess.edict.data.local.entity.ArticleEntity
import com.ochess.edict.data.local.entity.BookmarkEntity
import com.ochess.edict.data.local.entity.CategoryEntity
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.data.local.entity.TestEntity

@TypeConverters(value = [MeaningConverter::class, SynonymConverter::class, LabelConverter::class])
@Database(entities = [BookmarkEntity::class,
                      HistoryEntity::class,
                      CategoryEntity::class,
                      ArticleEntity::class,
                      TestEntity::class
                     ], exportSchema = true, version = 1)
abstract class WordModelDatabase: RoomDatabase() {
    abstract val wordModelDao: WordModelDao
    abstract val category: CategoryDao
    abstract val article: ArticleDao
    abstract val test: TestDao
}
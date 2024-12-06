package com.ochess.edict.di

import android.content.Context
import androidx.room.Room
import com.ochess.edict.data.DictionaryDatabase
import com.ochess.edict.data.WordModelDatabase
import com.ochess.edict.data.local.ArticleDao
import com.ochess.edict.data.local.CategoryDao
import com.ochess.edict.data.local.DictionaryDao
import com.ochess.edict.data.local.LevelDao
import com.ochess.edict.data.local.WordModelDao
import com.ochess.edict.data.repository.DictionaryRepository
import com.ochess.edict.data.repository.WordRepository
import com.ochess.edict.domain.repository.DictionaryBaseRepository
import com.ochess.edict.domain.repository.WordBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideWordRepository(
        wordModelDao: WordModelDao
    ): WordBaseRepository {
        return WordRepository(wordModelDao)
    }

    @Provides
    @Singleton
    fun provideDictRepository(
        dictionaryDao: DictionaryDao,
        levelDao: LevelDao,
        wordModelDao: WordModelDao
    ): DictionaryBaseRepository {
        return DictionaryRepository(dictionaryDao, levelDao, wordModelDao)
    }

    @Provides
    @Singleton
    fun provideWordModelDao(wordModelDatabase: WordModelDatabase): WordModelDao {
        return wordModelDatabase.wordModelDao
    }

    @Provides
    @Singleton
    fun provideDictionaryDao(dictionaryDatabase: DictionaryDatabase): DictionaryDao {
        return dictionaryDatabase.dictionaryDao
    }

    @Provides
    @Singleton
    fun provideLevelDao(dictionaryDatabase: DictionaryDatabase): LevelDao {
        return dictionaryDatabase.levelDao
    }


    @Provides
    @Singleton
    fun provideCategoryDao(database: WordModelDatabase): CategoryDao {
        return database.category
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: WordModelDatabase): ArticleDao {
        return database.article
    }
//    @Provides
//    @Singleton
//    fun provideChineseWordDao(dictionaryDatabase: DictionaryDatabase): ChineseWordDao {
//        return dictionaryDatabase.chineseWordDao
//    }

    @Provides
    @Singleton
    fun provideDictionaryDatabase(@ApplicationContext appContext: Context): DictionaryDatabase {
        return Room.databaseBuilder(appContext, DictionaryDatabase::class.java, "dictionaryDb")
            .fallbackToDestructiveMigration()
//            .createFromAsset("wordset/wordDB")
            .createFromAsset("wordset/wordDB_small")
            .build()
    }

    @Provides
    @Singleton
    fun provideWordModelDatabase(@ApplicationContext appContext: Context): WordModelDatabase {
        return Room.databaseBuilder(appContext, WordModelDatabase::class.java, "wordModelDb")
            .build()
    }

}
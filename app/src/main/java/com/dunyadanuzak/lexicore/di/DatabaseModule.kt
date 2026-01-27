package com.dunyadanuzak.lexicore.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dunyadanuzak.lexicore.data.AppDatabase
import com.dunyadanuzak.lexicore.data.WordDao
import com.dunyadanuzak.lexicore.data.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val dbFile = context.getDatabasePath("lexicore.db")
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lexicore.db"
        )
        .createFromAsset("database/lexicore.db")
        .addCallback(object : RoomDatabase.Callback() {
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                dbFile.delete()
            }
        })
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: AppDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }
}

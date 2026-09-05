package com.ndichan.app.di

import android.content.Context
import androidx.room.Room
import com.ndichan.app.core.common.Constants
import com.ndichan.app.data.local.NDiChanDatabase
import com.ndichan.app.data.local.dao.BookmarkDao
import com.ndichan.app.data.local.dao.HistoryDao
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
    fun provideDatabase(@ApplicationContext context: Context): NDiChanDatabase {
        return Room.databaseBuilder(
            context,
            NDiChanDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(db: NDiChanDatabase): BookmarkDao = db.bookmarkDao

    @Provides
    @Singleton
    fun provideHistoryDao(db: NDiChanDatabase): HistoryDao = db.historyDao
}

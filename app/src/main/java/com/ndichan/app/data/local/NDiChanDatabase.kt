package com.ndichan.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ndichan.app.data.local.dao.BookmarkDao
import com.ndichan.app.data.local.dao.HistoryDao
import com.ndichan.app.data.local.entity.BookmarkEntity
import com.ndichan.app.data.local.entity.HistoryEntity

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NDiChanDatabase : RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao
    abstract val historyDao: HistoryDao
}

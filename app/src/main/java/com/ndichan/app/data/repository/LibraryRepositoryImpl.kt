package com.ndichan.app.data.repository

import com.ndichan.app.data.local.dao.BookmarkDao
import com.ndichan.app.data.local.dao.HistoryDao
import com.ndichan.app.data.local.entity.BookmarkEntity
import com.ndichan.app.data.local.entity.HistoryEntity
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val historyDao: HistoryDao
) : LibraryRepository {

    override fun getAllBookmarks(): Flow<List<BookmarkItem>> {
        return bookmarkDao.getAllBookmarks().map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getBookmarksByType(type: MediaType): Flow<List<BookmarkItem>> {
        return bookmarkDao.getBookmarksByType(type.name).map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun isBookmarked(id: String): Flow<Boolean> {
        return bookmarkDao.isBookmarked(id).flowOn(Dispatchers.IO)
    }

    override suspend fun toggleBookmark(item: BookmarkItem) = withContext(Dispatchers.IO) {
        val exists = bookmarkDao.isBookmarkedSync(item.id)
        if (exists) {
            bookmarkDao.deleteBookmark(item.id)
        } else {
            bookmarkDao.insertBookmark(BookmarkEntity.fromDomain(item))
        }
    }

    override suspend fun removeBookmark(id: String) = withContext(Dispatchers.IO) {
        bookmarkDao.deleteBookmark(id)
    }

    override fun getAllHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory().map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getHistoryByType(type: MediaType): Flow<List<HistoryItem>> {
        return historyDao.getHistoryByType(type.name).map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getHistoryItem(id: String): Flow<HistoryItem?> {
        return historyDao.getHistoryItem(id).map { it?.toDomain() }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveHistory(item: HistoryItem) = withContext(Dispatchers.IO) {
        historyDao.insertHistory(HistoryEntity.fromDomain(item))
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAll()
    }
}

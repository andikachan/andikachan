package com.ndichan.app.domain.repository

import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getAllBookmarks(): Flow<List<BookmarkItem>>
    fun getBookmarksByType(type: MediaType): Flow<List<BookmarkItem>>
    fun isBookmarked(id: String): Flow<Boolean>
    suspend fun toggleBookmark(item: BookmarkItem)
    suspend fun removeBookmark(id: String)

    fun getAllHistory(): Flow<List<HistoryItem>>
    fun getHistoryByType(type: MediaType): Flow<List<HistoryItem>>
    fun getHistoryItem(id: String): Flow<HistoryItem?>
    suspend fun saveHistory(item: HistoryItem)
    suspend fun clearHistory()
}

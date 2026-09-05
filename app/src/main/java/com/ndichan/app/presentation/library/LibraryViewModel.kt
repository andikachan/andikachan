package com.ndichan.app.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LibraryTab(val label: String) {
    BOOKMARKS("Favorit"),
    HISTORY("Riwayat")
}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.BOOKMARKS,
    val filterType: MediaType? = null, // null means all
    val bookmarks: List<BookmarkItem> = emptyList(),
    val history: List<HistoryItem> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observeBookmarks()
        observeHistory()
    }

    private fun observeBookmarks() {
        libraryRepository.getAllBookmarks().onEach { list ->
            _uiState.update { it.copy(bookmarks = list) }
        }.launchIn(viewModelScope)
    }

    private fun observeHistory() {
        libraryRepository.getAllHistory().onEach { list ->
            _uiState.update { it.copy(history = list) }
        }.launchIn(viewModelScope)
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setFilterType(type: MediaType?) {
        _uiState.update { it.copy(filterType = type) }
    }

    fun removeBookmark(id: String) {
        viewModelScope.launch {
            libraryRepository.removeBookmark(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            libraryRepository.clearHistory()
        }
    }

    fun getFilteredBookmarks(): List<BookmarkItem> {
        val filter = _uiState.value.filterType
        return if (filter == null) {
            _uiState.value.bookmarks
        } else {
            _uiState.value.bookmarks.filter { it.mediaType == filter }
        }
    }

    fun getFilteredHistory(): List<HistoryItem> {
        val filter = _uiState.value.filterType
        return if (filter == null) {
            _uiState.value.history
        } else {
            _uiState.value.history.filter { it.mediaType == filter }
        }
    }
}

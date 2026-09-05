package com.ndichan.app.presentation.manga.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MangaChapter
import com.ndichan.app.domain.model.MangaReading
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.LibraryRepository
import com.ndichan.app.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MangaReaderUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val readingData: MangaReading? = null,
    val isControlsVisible: Boolean = true,
    val currentPageIndex: Int = 1,
    val totalPages: Int = 1,
    val showChapterListSheet: Boolean = false
)

@HiltViewModel
class MangaReaderViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var chapterSlug: String = checkNotNull(savedStateHandle["chapterSlug"])
        private set

    var mangaSlug: String = savedStateHandle["mangaSlug"] ?: ""
        private set

    private val _uiState = MutableStateFlow(MangaReaderUiState())
    val uiState: StateFlow<MangaReaderUiState> = _uiState.asStateFlow()

    init {
        loadReadingPage(chapterSlug)
    }

    fun loadReadingPage(slug: String) {
        chapterSlug = slug
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            mangaRepository.getMangaReading(slug).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val d = res.data
                        val pageCount = d?.pages?.size ?: 1
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                readingData = d,
                                totalPages = pageCount,
                                currentPageIndex = 1,
                                errorMessage = null
                            )
                        }
                        saveHistory(1, pageCount)
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = res.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun toggleControls() {
        _uiState.update { it.copy(isControlsVisible = !it.isControlsVisible) }
    }

    fun updateCurrentPage(index: Int) {
        val total = _uiState.value.totalPages
        _uiState.update { it.copy(currentPageIndex = index.coerceIn(1, total)) }
        saveHistory(index, total)
    }

    fun setChapterListSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showChapterListSheet = visible) }
    }

    fun getNextChapter(): MangaChapter? {
        val otherChapters = _uiState.value.readingData?.otherChapters ?: return null
        val currentSlug = chapterSlug
        val currentIndex = otherChapters.indexOfFirst { it.slug == currentSlug }
        if (currentIndex > 0) {
            return otherChapters[currentIndex - 1]
        }
        return null
    }

    fun getPreviousChapter(): MangaChapter? {
        val otherChapters = _uiState.value.readingData?.otherChapters ?: return null
        val currentSlug = chapterSlug
        val currentIndex = otherChapters.indexOfFirst { it.slug == currentSlug }
        if (currentIndex != -1 && currentIndex < otherChapters.size - 1) {
            return otherChapters[currentIndex + 1]
        }
        return null
    }

    private fun saveHistory(page: Int, totalPages: Int) {
        val data = _uiState.value.readingData ?: return
        viewModelScope.launch {
            libraryRepository.saveHistory(
                HistoryItem(
                    id = mangaSlug.ifBlank { data.mangaSlug },
                    mediaType = MediaType.MANGA,
                    title = data.title,
                    coverUrl = null,
                    lastItemTitle = data.chapterTitle,
                    lastItemId = chapterSlug,
                    progress = page.toLong(),
                    totalProgress = totalPages.toLong(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

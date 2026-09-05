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
import java.net.URLDecoder
import java.util.Locale
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

    var mangaTitle: String = run {
        val raw = savedStateHandle["mangaTitle"] ?: ""
        try { URLDecoder.decode(raw, "UTF-8") } catch (_: Exception) { raw }
    }
        private set

    var coverUrl: String = run {
        val raw = savedStateHandle["coverUrl"] ?: ""
        try { URLDecoder.decode(raw, "UTF-8") } catch (_: Exception) { raw }
    }
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
                        val pageCount = (d?.pages?.size ?: 1).coerceAtLeast(1)

                        val resolvedTitle = d?.title?.takeIf { it.isNotBlank() }
                            ?: mangaTitle.takeIf { it.isNotBlank() }
                            ?: (d?.mangaSlug ?: mangaSlug).replace("-", " ")
                                .split(" ")
                                .joinToString(" ") { it.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) } }

                        val resolvedChapterTitle = d?.chapterTitle?.takeIf { it.isNotBlank() }
                            ?: if (!d?.chapterNum.isNullOrBlank()) "Chapter ${d?.chapterNum}" else "Chapter"

                        val resolvedReading = d?.copy(
                            title = resolvedTitle,
                            chapterTitle = resolvedChapterTitle
                        )

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                readingData = resolvedReading,
                                totalPages = pageCount,
                                currentPageIndex = 1,
                                errorMessage = null
                            )
                        }
                        saveHistory(1, pageCount, resolvedReading)
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
        val page = index.coerceIn(1, total)
        _uiState.update { it.copy(currentPageIndex = page) }
        saveHistory(page, total, _uiState.value.readingData)
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

    private fun saveHistory(page: Int, totalPages: Int, reading: MangaReading?) {
        val data = reading ?: _uiState.value.readingData ?: return
        val targetSlug = mangaSlug.ifBlank { data.mangaSlug.ifBlank { chapterSlug.substringBefore("-chapter-") } }
        val displayTitle = data.title.ifBlank {
            mangaTitle.ifBlank {
                targetSlug.replace("-", " ")
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) } }
            }
        }
        val displayChapter = data.chapterTitle.ifBlank { "Chapter ${data.chapterNum}" }

        viewModelScope.launch {
            libraryRepository.saveHistory(
                HistoryItem(
                    id = targetSlug,
                    mediaType = MediaType.MANGA,
                    title = displayTitle,
                    coverUrl = coverUrl.ifBlank { null },
                    lastItemTitle = displayChapter,
                    lastItemId = chapterSlug,
                    progress = page.toLong(),
                    totalProgress = totalPages.toLong(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

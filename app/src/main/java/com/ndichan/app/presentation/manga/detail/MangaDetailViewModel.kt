package com.ndichan.app.presentation.manga.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.MangaChapter
import com.ndichan.app.domain.model.MangaDetail
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.LibraryRepository
import com.ndichan.app.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MangaDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val mangaDetail: MangaDetail? = null,
    val isBookmarked: Boolean = false,
    val isReversed: Boolean = false,
    val chapterSearchQuery: String = ""
)

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mangaSlug: String = checkNotNull(savedStateHandle["mangaSlug"])

    private val _uiState = MutableStateFlow(MangaDetailUiState())
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        observeBookmarkStatus()
    }

    private fun observeBookmarkStatus() {
        libraryRepository.isBookmarked(mangaSlug).onEach { isBookmarked ->
            _uiState.update { it.copy(isBookmarked = isBookmarked) }
        }.launchIn(viewModelScope)
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            mangaRepository.getMangaDetail(mangaSlug).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                mangaDetail = res.data,
                                errorMessage = null
                            )
                        }
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

    fun toggleBookmark() {
        val detail = _uiState.value.mangaDetail ?: return
        viewModelScope.launch {
            val item = BookmarkItem(
                id = detail.slug,
                mediaType = MediaType.MANGA,
                title = detail.title,
                coverUrl = detail.coverUrl,
                extraInfo = detail.status ?: "${detail.chapters.size} Chapter",
                timestamp = System.currentTimeMillis()
            )
            libraryRepository.toggleBookmark(item)
        }
    }

    fun toggleChapterOrder() {
        _uiState.update { it.copy(isReversed = !it.isReversed) }
    }

    fun updateChapterSearch(query: String) {
        _uiState.update { it.copy(chapterSearchQuery = query) }
    }

    fun getFilteredChapters(): List<MangaChapter> {
        val detail = _uiState.value.mangaDetail ?: return emptyList()
        val query = _uiState.value.chapterSearchQuery.trim().lowercase()

        var list = if (query.isEmpty()) {
            detail.chapters
        } else {
            detail.chapters.filter {
                it.chapterNum.contains(query) || it.slug.lowercase().contains(query)
            }
        }

        if (_uiState.value.isReversed) {
            list = list.reversed()
        }

        return list
    }
}

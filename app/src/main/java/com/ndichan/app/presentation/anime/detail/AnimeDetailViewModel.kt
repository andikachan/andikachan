package com.ndichan.app.presentation.anime.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.AnimeDetail
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.Episode
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.AnimeRepository
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

data class AnimeDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val animeDetail: AnimeDetail? = null,
    val isBookmarked: Boolean = false,
    val isReversed: Boolean = false,
    val episodeSearchQuery: String = ""
)

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId: String = checkNotNull(savedStateHandle["animeId"])

    private val _uiState = MutableStateFlow(AnimeDetailUiState())
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        observeBookmarkStatus()
    }

    private fun observeBookmarkStatus() {
        libraryRepository.isBookmarked(animeId).onEach { isBookmarked ->
            _uiState.update { it.copy(isBookmarked = isBookmarked) }
        }.launchIn(viewModelScope)
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            animeRepository.getAnimeDetail(animeId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                animeDetail = res.data,
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
        val detail = _uiState.value.animeDetail ?: return
        viewModelScope.launch {
            val item = BookmarkItem(
                id = detail.id,
                mediaType = MediaType.ANIME,
                title = detail.title,
                coverUrl = detail.posterUrl ?: detail.coverUrl,
                extraInfo = detail.status ?: "${detail.episodes.size} Episode",
                timestamp = System.currentTimeMillis()
            )
            libraryRepository.toggleBookmark(item)
        }
    }

    fun toggleEpisodeOrder() {
        _uiState.update { it.copy(isReversed = !it.isReversed) }
    }

    fun updateEpisodeSearch(query: String) {
        _uiState.update { it.copy(episodeSearchQuery = query) }
    }

    fun getFilteredEpisodes(): List<Episode> {
        val detail = _uiState.value.animeDetail ?: return emptyList()
        val query = _uiState.value.episodeSearchQuery.trim().lowercase()

        var list = if (query.isEmpty()) {
            detail.episodes
        } else {
            detail.episodes.filter {
                it.title.lowercase().contains(query) || it.index.contains(query)
            }
        }

        if (_uiState.value.isReversed) {
            list = list.reversed()
        }

        return list
    }
}

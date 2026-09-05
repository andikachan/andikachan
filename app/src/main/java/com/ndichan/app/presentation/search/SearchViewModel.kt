package com.ndichan.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.Anime
import com.ndichan.app.domain.model.Manga
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.repository.AnimeRepository
import com.ndichan.app.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val activeMediaType: MediaType = MediaType.ANIME,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val animeResults: List<Anime> = emptyList(),
    val mangaResults: List<Manga> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()

        if (newQuery.trim().isEmpty()) {
            _uiState.update { it.copy(animeResults = emptyList(), mangaResults = emptyList(), isLoading = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L) // Debounce typing
            performSearch(newQuery.trim())
        }
    }

    fun setMediaType(type: MediaType) {
        _uiState.update { it.copy(activeMediaType = type) }
        if (_uiState.value.query.trim().isNotEmpty()) {
            performSearch(_uiState.value.query.trim())
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            if (_uiState.value.activeMediaType == MediaType.ANIME) {
                animeRepository.searchAnime(query = query, page = 0).collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    animeResults = res.data ?: emptyList(),
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
            } else {
                mangaRepository.searchManga(query = query).collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    mangaResults = res.data ?: emptyList(),
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
    }
}

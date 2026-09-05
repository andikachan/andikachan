package com.ndichan.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.repository.AnimeRepository
import com.ndichan.app.domain.repository.LibraryRepository
import com.ndichan.app.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        observeHistory()
    }

    private fun observeHistory() {
        libraryRepository.getAllHistory().onEach { historyList ->
            _uiState.update { it.copy(recentHistory = historyList.take(3)) }
        }.launchIn(viewModelScope)
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val heroDeferred = async { mangaRepository.getHeroSlider(5) }
            val popularAnimeDeferred = async { animeRepository.getPopularAnime(1) }
            val newAnimeDeferred = async { animeRepository.getNewEpisodes(1, 15) }
            val popularMangaDeferred = async { mangaRepository.getPopularToday(15) }
            val latestMangaDeferred = async { mangaRepository.getLatest(15) }

            // Collect Hero Slider
            heroDeferred.await().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(heroSliderManga = res.data ?: emptyList()) }
                }
            }

            // Collect Popular Anime
            popularAnimeDeferred.await().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(popularAnime = res.data ?: emptyList()) }
                }
            }

            // Collect New Anime
            newAnimeDeferred.await().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(newEpisodeAnime = res.data ?: emptyList()) }
                }
            }

            // Collect Popular Manga
            popularMangaDeferred.await().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(popularTodayManga = res.data ?: emptyList()) }
                }
            }

            // Collect Latest Manga
            latestMangaDeferred.await().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(latestManga = res.data ?: emptyList()) }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

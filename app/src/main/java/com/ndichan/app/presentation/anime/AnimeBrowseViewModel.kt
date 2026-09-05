package com.ndichan.app.presentation.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.Anime
import com.ndichan.app.domain.model.AnimeGenre
import com.ndichan.app.domain.model.ScheduleDay
import com.ndichan.app.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AnimeTab(val label: String) {
    POPULAR("Populer"),
    NEW_EPISODES("Episode Baru"),
    ONGOING("Sedang Tayang"),
    SCHEDULE("Jadwal Rilis"),
    GENRES("Genre")
}

data class AnimeBrowseUiState(
    val selectedTab: AnimeTab = AnimeTab.POPULAR,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val animeList: List<Anime> = emptyList(),
    val genres: List<AnimeGenre> = emptyList(),
    val selectedGenreId: String? = null,
    val schedule: List<ScheduleDay> = emptyList(),
    val selectedDayIndex: Int = 0
)

@HiltViewModel
class AnimeBrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeBrowseUiState())
    val uiState: StateFlow<AnimeBrowseUiState> = _uiState.asStateFlow()

    init {
        loadDataForCurrentTab()
        loadGenres()
    }

    fun selectTab(tab: AnimeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        loadDataForCurrentTab()
    }

    fun selectGenre(genreId: String) {
        _uiState.update { it.copy(selectedGenreId = genreId) }
        loadAnimeByGenre(genreId)
    }

    fun selectDay(index: Int) {
        _uiState.update { it.copy(selectedDayIndex = index) }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            animeRepository.getGenres().collect { res ->
                if (res is Resource.Success) {
                    val genreList = res.data ?: emptyList()
                    _uiState.update { it.copy(genres = genreList) }
                    if (_uiState.value.selectedGenreId == null && genreList.isNotEmpty()) {
                        _uiState.update { it.copy(selectedGenreId = genreList.first().id) }
                    }
                }
            }
        }
    }

    private fun loadDataForCurrentTab() {
        when (_uiState.value.selectedTab) {
            AnimeTab.POPULAR -> loadPopular()
            AnimeTab.NEW_EPISODES -> loadNewEpisodes()
            AnimeTab.ONGOING -> loadOngoing()
            AnimeTab.SCHEDULE -> loadSchedule()
            AnimeTab.GENRES -> {
                _uiState.value.selectedGenreId?.let { loadAnimeByGenre(it) }
            }
        }
    }

    private fun loadPopular() {
        viewModelScope.launch {
            animeRepository.getPopularAnime(1).collect { res ->
                handleAnimeResource(res)
            }
        }
    }

    private fun loadNewEpisodes() {
        viewModelScope.launch {
            animeRepository.getNewEpisodes(1, 30).collect { res ->
                handleAnimeResource(res)
            }
        }
    }

    private fun loadOngoing() {
        viewModelScope.launch {
            animeRepository.getOngoingAnime().collect { res ->
                handleAnimeResource(res)
            }
        }
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            animeRepository.getSchedule().collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                schedule = res.data ?: emptyList(),
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

    private fun loadAnimeByGenre(genreId: String) {
        viewModelScope.launch {
            animeRepository.getAnimeByGenre(genreId, 1).collect { res ->
                handleAnimeResource(res)
            }
        }
    }

    private fun handleAnimeResource(res: Resource<List<Anime>>) {
        when (res) {
            is Resource.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        animeList = res.data ?: emptyList(),
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

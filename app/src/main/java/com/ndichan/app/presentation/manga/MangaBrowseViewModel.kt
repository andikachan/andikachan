package com.ndichan.app.presentation.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.Manga
import com.ndichan.app.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MangaTab(val label: String) {
    POPULAR_TODAY("Populer Hari Ini"),
    LATEST("Update Terbaru"),
    PROJECT("Project Unggulan"),
    ALL_COMICS("Semua Komik"),
    FILTER("Filter Genre")
}

data class MangaBrowseUiState(
    val selectedTab: MangaTab = MangaTab.POPULAR_TODAY,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val mangaList: List<Manga> = emptyList(),
    val selectedGenre: String = "action",
    val selectedType: String? = null,
    val selectedStatus: String? = null,
    val selectedOrderBy: String = "popular"
)

@HiltViewModel
class MangaBrowseViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MangaBrowseUiState())
    val uiState: StateFlow<MangaBrowseUiState> = _uiState.asStateFlow()

    init {
        loadCurrentTab()
    }

    fun selectTab(tab: MangaTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        loadCurrentTab()
    }

    fun setGenreFilter(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
        loadFilter()
    }

    fun setTypeFilter(type: String?) {
        _uiState.update { it.copy(selectedType = type) }
        loadFilter()
    }

    fun setStatusFilter(status: String?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadFilter()
    }

    fun setOrderFilter(orderBy: String) {
        _uiState.update { it.copy(selectedOrderBy = orderBy) }
        loadFilter()
    }

    private fun loadCurrentTab() {
        when (_uiState.value.selectedTab) {
            MangaTab.POPULAR_TODAY -> loadPopularToday()
            MangaTab.LATEST -> loadLatest()
            MangaTab.PROJECT -> loadProject()
            MangaTab.ALL_COMICS -> loadAllComics()
            MangaTab.FILTER -> loadFilter()
        }
    }

    private fun loadPopularToday() {
        viewModelScope.launch {
            mangaRepository.getPopularToday(24).collect { res ->
                handleMangaResource(res)
            }
        }
    }

    private fun loadLatest() {
        viewModelScope.launch {
            mangaRepository.getLatest(30).collect { res ->
                handleMangaResource(res)
            }
        }
    }

    private fun loadProject() {
        viewModelScope.launch {
            mangaRepository.getLatestProject(24).collect { res ->
                handleMangaResource(res)
            }
        }
    }

    private fun loadAllComics() {
        viewModelScope.launch {
            mangaRepository.getAllComics(30).collect { res ->
                handleMangaResource(res)
            }
        }
    }

    private fun loadFilter() {
        viewModelScope.launch {
            mangaRepository.filterManga(
                genreSlug = _uiState.value.selectedGenre,
                status = _uiState.value.selectedStatus,
                type = _uiState.value.selectedType,
                orderBy = _uiState.value.selectedOrderBy,
                limit = 24
            ).collect { res ->
                handleMangaResource(res)
            }
        }
    }

    private fun handleMangaResource(res: Resource<List<Manga>>) {
        when (res) {
            is Resource.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mangaList = res.data ?: emptyList(),
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

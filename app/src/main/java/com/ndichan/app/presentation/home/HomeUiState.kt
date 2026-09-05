package com.ndichan.app.presentation.home

import com.ndichan.app.domain.model.Anime
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.Manga

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val heroSliderManga: List<Manga> = emptyList(),
    val popularAnime: List<Anime> = emptyList(),
    val newEpisodeAnime: List<Anime> = emptyList(),
    val popularTodayManga: List<Manga> = emptyList(),
    val latestManga: List<Manga> = emptyList(),
    val recentHistory: List<HistoryItem> = emptyList()
)

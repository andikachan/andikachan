package com.ndichan.app.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ndichan.app.core.components.AnimeCard
import com.ndichan.app.core.components.EmptyState
import com.ndichan.app.core.components.ErrorView
import com.ndichan.app.core.components.MangaCard
import com.ndichan.app.core.components.ShimmerCard
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import com.ndichan.app.domain.model.MediaType

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetail: (String) -> Unit,
    onNavigateToMangaDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Search Top Header with Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextPrimary
                )
            }

            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = {
                    Text(
                        text = if (state.activeMediaType == MediaType.ANIME) "Cari anime..." else "Cari komik / manga...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Hapus",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }

        // Toggle Pill for Media Type (Anime vs Manga)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(BgCard)
                    .border(1.dp, BorderSubtle, PillShape)
                    .padding(4.dp)
            ) {
                Row {
                    val isAnimeSelected = state.activeMediaType == MediaType.ANIME
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (isAnimeSelected) GoldPrimary else Color.Transparent)
                            .clickable { viewModel.setMediaType(MediaType.ANIME) }
                            .padding(horizontal = 24.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Anime",
                            color = if (isAnimeSelected) BgPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isAnimeSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    val isMangaSelected = state.activeMediaType == MediaType.MANGA
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (isMangaSelected) GoldPrimary else Color.Transparent)
                            .clickable { viewModel.setMediaType(MediaType.MANGA) }
                            .padding(horizontal = 24.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Manga",
                            color = if (isMangaSelected) BgPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isMangaSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Main Results Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (state.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(9) {
                        ShimmerCard(height = 160.dp)
                    }
                }
            } else if (state.errorMessage != null) {
                ErrorView(
                    message = state.errorMessage ?: "Pencarian gagal",
                    onRetry = { viewModel.onQueryChange(state.query) },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.query.trim().isEmpty()) {
                EmptyState(
                    title = "Cari Judul",
                    description = "Ketik judul anime atau komik yang ingin Anda cari.",
                    icon = Icons.Outlined.Search,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.activeMediaType == MediaType.ANIME) {
                if (state.animeResults.isEmpty()) {
                    EmptyState(
                        title = "Anime Tidak Ditemukan",
                        description = "Tidak ada hasil anime untuk '${state.query}'",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(110.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.animeResults) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onNavigateToAnimeDetail(anime.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                if (state.mangaResults.isEmpty()) {
                    EmptyState(
                        title = "Komik Tidak Ditemukan",
                        description = "Tidak ada hasil komik untuk '${state.query}'",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(110.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.mangaResults) { manga ->
                            MangaCard(
                                manga = manga,
                                onClick = { onNavigateToMangaDetail(manga.slug) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

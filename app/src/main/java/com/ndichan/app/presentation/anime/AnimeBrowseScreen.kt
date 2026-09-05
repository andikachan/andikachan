package com.ndichan.app.presentation.anime

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.ndichan.app.core.components.NDiChanTopBar
import com.ndichan.app.core.components.ShimmerCard
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.ChipShape
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary

@Composable
fun AnimeBrowseScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: AnimeBrowseViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        NDiChanTopBar(
            title = "Katalog Anime",
            actions = {
                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Cari",
                        tint = TextPrimary
                    )
                }
            }
        )

        // Main Tab Row
        ScrollableTabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = BgPrimary,
            contentColor = GoldPrimary,
            edgePadding = 16.dp,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                    color = GoldPrimary,
                    height = 2.dp
                )
            }
        ) {
            AnimeTab.values().forEach { tab ->
                val isSelected = state.selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            text = tab.label,
                            color = if (isSelected) GoldPrimary else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Sub Filter: Genre Chips if Genre Tab is active
        if (state.selectedTab == AnimeTab.GENRES && state.genres.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.genres) { genre ->
                    val isSelected = state.selectedGenreId == genre.id
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (isSelected) GoldPrimary else BgCard)
                            .border(
                                1.dp,
                                if (isSelected) GoldPrimary else BorderSubtle,
                                PillShape
                            )
                            .clickable { viewModel.selectGenre(genre.id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = genre.name,
                            color = if (isSelected) BgPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Sub Filter: Schedule Day Chips if Schedule Tab is active
        if (state.selectedTab == AnimeTab.SCHEDULE && state.schedule.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.schedule) { index, day ->
                    val isSelected = state.selectedDayIndex == index
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (isSelected) GoldPrimary else BgCard)
                            .border(
                                1.dp,
                                if (isSelected) GoldPrimary else BorderSubtle,
                                PillShape
                            )
                            .clickable { viewModel.selectDay(index) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = day.dayName,
                            color = if (isSelected) BgPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Main Content Area
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
                    message = state.errorMessage ?: "Gagal memuat",
                    onRetry = { viewModel.selectTab(state.selectedTab) },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.selectedTab == AnimeTab.SCHEDULE) {
                val currentDay = state.schedule.getOrNull(state.selectedDayIndex)
                val animesForDay = currentDay?.animes ?: emptyList()

                if (animesForDay.isEmpty()) {
                    EmptyState(
                        title = "Tidak Ada Jadwal",
                        description = "Belum ada anime yang dijadwalkan tayang pada hari ini.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(110.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(animesForDay) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onNavigateToDetail(anime.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                if (state.animeList.isEmpty()) {
                    EmptyState(
                        title = "Katalog Kosong",
                        description = "Tidak ada anime yang ditemukan untuk kategori ini.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(110.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.animeList) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onNavigateToDetail(anime.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.ndichan.app.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ndichan.app.core.components.ErrorView
import com.ndichan.app.core.components.HeroBanner
import com.ndichan.app.core.components.MangaCard
import com.ndichan.app.core.components.QuickResumeCard
import com.ndichan.app.core.components.SectionHeader
import com.ndichan.app.core.components.ShimmerCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import com.ndichan.app.domain.model.MediaType

@Composable
fun HomeScreen(
    onNavigateToAnimeDetail: (String) -> Unit,
    onNavigateToMangaDetail: (String) -> Unit,
    onNavigateToVideoPlayer: (String, String, String) -> Unit,
    onNavigateToMangaReader: (String, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAnimeBrowse: () -> Unit,
    onNavigateToMangaBrowse: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        if (state.isLoading && state.heroSliderManga.isEmpty() && state.popularAnime.isEmpty()) {
            // Shimmer Loading Screen
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    ShimmerCard(height = 36.dp, modifier = Modifier.width(160.dp))
                }
                item {
                    ShimmerCard(height = 200.dp)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) {
                            ShimmerCard(height = 180.dp, modifier = Modifier.width(120.dp))
                        }
                    }
                }
                item {
                    ShimmerCard(height = 140.dp)
                }
            }
        } else if (state.errorMessage != null && state.popularAnime.isEmpty()) {
            ErrorView(
                message = state.errorMessage ?: "Terjadi kesalahan",
                onRetry = { viewModel.loadHomeData() },
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Top App Bar / Brand Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NDiChan",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Streaming & Baca Komik Premium",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = onNavigateToSearch,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF15171C))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Cari",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Recent History / Continue Watching & Reading Bar
                if (state.recentHistory.isNotEmpty()) {
                    item {
                        val latestHistory = state.recentHistory.first()
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            QuickResumeCard(
                                historyItem = latestHistory,
                                onClick = {
                                    if (latestHistory.mediaType == MediaType.ANIME) {
                                        onNavigateToVideoPlayer(
                                            latestHistory.lastItemId,
                                            latestHistory.id,
                                            latestHistory.lastItemTitle
                                        )
                                    } else {
                                        onNavigateToMangaReader(
                                            latestHistory.lastItemId,
                                            latestHistory.id
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Hero Slider Carousel
                if (state.heroSliderManga.isNotEmpty()) {
                    item {
                        val pagerState = rememberPagerState(pageCount = { state.heroSliderManga.size })
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                pageSpacing = 12.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                val manga = state.heroSliderManga[page]
                                HeroBanner(
                                    title = manga.title,
                                    coverUrl = manga.coverUrl,
                                    subtitle = manga.summary?.replace(Regex("<[^>]*>"), "")?.trim(),
                                    tag = manga.genre ?: manga.badge ?: "Unggulan",
                                    buttonText = "Baca Sekarang",
                                    onButtonClick = { onNavigateToMangaDetail(manga.slug) }
                                )
                            }

                            // Pager Indicator Dots
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val isSelected = pagerState.currentPage == iteration
                                    Box(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .size(if (isSelected) 16.dp to 4.dp else 4.dp to 4.dp)
                                            .clip(PillShape)
                                            .background(if (isSelected) GoldPrimary else Color(0xFF252832))
                                    )
                                }
                            }
                        }
                    }
                }

                // Anime Episode Terbaru Section
                if (state.newEpisodeAnime.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Episode Terbaru",
                            actionText = "Lihat Semua",
                            onActionClick = onNavigateToAnimeBrowse
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.newEpisodeAnime) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onNavigateToAnimeDetail(anime.id) }
                                )
                            }
                        }
                    }
                }

                // Manga Populer Hari Ini Section
                if (state.popularTodayManga.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "Manga Populer Hari Ini",
                            actionText = "Lihat Semua",
                            onActionClick = onNavigateToMangaBrowse
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.popularTodayManga) { manga ->
                                MangaCard(
                                    manga = manga,
                                    onClick = { onNavigateToMangaDetail(manga.slug) }
                                )
                            }
                        }
                    }
                }

                // Anime Terpopuler Section
                if (state.popularAnime.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "Anime Terpopuler",
                            actionText = "Lihat Semua",
                            onActionClick = onNavigateToAnimeBrowse
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.popularAnime) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onNavigateToAnimeDetail(anime.id) }
                                )
                            }
                        }
                    }
                }

                // Manga Rilis Terbaru Section
                if (state.latestManga.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "Update Komik Terbaru",
                            actionText = "Lihat Semua",
                            onActionClick = onNavigateToMangaBrowse
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.latestManga) { manga ->
                                MangaCard(
                                    manga = manga,
                                    onClick = { onNavigateToMangaDetail(manga.slug) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

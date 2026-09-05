package com.ndichan.app.presentation.manga.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ndichan.app.core.components.EmptyState
import com.ndichan.app.core.components.ErrorView
import com.ndichan.app.core.components.MangaCard
import com.ndichan.app.core.components.SectionHeader
import com.ndichan.app.core.components.ShimmerCard
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.CardShape
import com.ndichan.app.core.theme.ChipShape
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.OverlayGradient
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import com.ndichan.app.domain.model.MangaChapter

@Composable
fun MangaDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReader: (String, String) -> Unit,
    onNavigateToOtherManga: (String) -> Unit,
    viewModel: MangaDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var isSynopsisExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())
                ShimmerCard(height = 220.dp)
                ShimmerCard(height = 40.dp)
                ShimmerCard(height = 100.dp)
            }
        } else if (state.errorMessage != null || state.mangaDetail == null) {
            ErrorView(
                message = state.errorMessage ?: "Komik tidak ditemukan",
                onRetry = { viewModel.loadDetail() },
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val detail = state.mangaDetail!!
            val filteredChapters = viewModel.getFilteredChapters()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Header Banner Image with Backdrop
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(detail.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = detail.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(OverlayGradient)
                        )

                        // Top Navigation Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x990D0E11))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = TextPrimary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleBookmark() },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x990D0E11))
                            ) {
                                Icon(
                                    imageVector = if (state.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Simpan",
                                    tint = if (state.isBookmarked) GoldPrimary else TextPrimary
                                )
                            }
                        }

                        // Poster thumbnail on bottom left
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .aspectRatio(2f / 3f)
                                    .clip(CardShape)
                                    .border(1.5.dp, GoldPrimary, CardShape)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(detail.coverUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = detail.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                if (!detail.status.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(ChipShape)
                                            .background(Color(0xCC0D0E11))
                                            .border(0.5.dp, Color(0x66D4AF37), ChipShape)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = detail.status,
                                            color = GoldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = detail.title,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                val metaInfo = listOfNotNull(detail.author, detail.type).joinToString(" | ")
                                if (metaInfo.isNotBlank()) {
                                    Text(
                                        text = metaInfo,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (!detail.rating.isNullOrBlank() && detail.rating != "0") {
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = detail.rating,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action CTA: Baca Chapter Pertama
                item {
                    if (detail.chapters.isNotEmpty()) {
                        val firstChapter = detail.chapters.lastOrNull() ?: detail.chapters.first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(GoldPrimary)
                                .clickable {
                                    onNavigateToReader(firstChapter.slug, detail.slug)
                                }
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = BgPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mulai Baca (Chapter ${firstChapter.chapterNum})",
                                color = BgPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Genres Chips
                if (detail.genres.isNotEmpty()) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detail.genres) { g ->
                                Box(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .background(BgCard)
                                        .border(1.dp, BorderSubtle, PillShape)
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = g,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Synopsis
                if (detail.synopsis.isNotBlank()) {
                    val cleanSynopsis = detail.synopsis.replace(Regex("<[^>]*>"), "").trim()
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(CardShape)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, CardShape)
                                .clickable { isSynopsisExpanded = !isSynopsisExpanded }
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Sinopsis Komik",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cleanSynopsis,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                maxLines = if (isSynopsisExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isSynopsisExpanded) "Tutup" else "Baca Selengkapnya",
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Related Manga Section
                if (detail.relatedManga.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Komik Terkait")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(detail.relatedManga) { related ->
                                MangaCard(
                                    manga = related,
                                    onClick = { onNavigateToOtherManga(related.slug) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Chapter Header & Search/Filter
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Chapter (${detail.chapters.size})",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Cari Chapter",
                                        tint = if (isSearchExpanded) GoldPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(onClick = { viewModel.toggleChapterOrder() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = "Urutkan",
                                        tint = if (state.isReversed) GoldPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Search box for chapters
                        AnimatedVisibility(visible = isSearchExpanded) {
                            OutlinedTextField(
                                value = state.chapterSearchQuery,
                                onValueChange = { viewModel.updateChapterSearch(it) },
                                placeholder = { Text("Cari nomor chapter...", fontSize = 12.sp, color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedContainerColor = BgCard,
                                    unfocusedContainerColor = BgCard,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                // Chapter List Items
                if (filteredChapters.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Chapter Tidak Ditemukan",
                            description = "Tidak ada chapter yang sesuai dengan pencarian."
                        )
                    }
                } else {
                    items(filteredChapters) { chapter ->
                        ChapterRowItem(
                            chapter = chapter,
                            onClick = {
                                onNavigateToReader(chapter.slug, detail.slug)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterRowItem(
    chapter: MangaChapter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardShape)
            .background(BgCard)
            .border(1.dp, BorderSubtle, CardShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Chapter ${chapter.chapterNum}",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        if (!chapter.time.isNullOrBlank()) {
            val displayTime = chapter.time.substringBefore("T")
            Text(
                text = displayTime,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

package com.ndichan.app.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.ndichan.app.core.components.NDiChanTopBar
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.CardShape
import com.ndichan.app.core.theme.ChipShape
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType

@Composable
fun LibraryScreen(
    onNavigateToAnimeDetail: (String) -> Unit,
    onNavigateToMangaDetail: (String) -> Unit,
    onNavigateToVideoPlayer: (String, String, String) -> Unit,
    onNavigateToMangaReader: (String, String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredBookmarks = viewModel.getFilteredBookmarks()
    val filteredHistory = viewModel.getFilteredHistory()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        NDiChanTopBar(
            title = "Koleksi Saya",
            actions = {
                if (state.selectedTab == LibraryTab.HISTORY && state.history.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearAllHistory() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus Riwayat",
                            tint = TextSecondary
                        )
                    }
                }
            }
        )

        // Main Tab Row (Favorit vs Riwayat)
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = BgPrimary,
            contentColor = GoldPrimary,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                    color = GoldPrimary,
                    height = 2.dp
                )
            }
        ) {
            LibraryTab.values().forEach { tab ->
                val isSelected = state.selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            text = tab.label,
                            color = if (isSelected) GoldPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Sub Filter Pills: Semua, Anime, Manga
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                null to "Semua",
                MediaType.ANIME to "Anime",
                MediaType.MANGA to "Manga"
            )

            filters.forEach { (type, label) ->
                val isSelected = state.filterType == type
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (isSelected) GoldPrimary else BgCard)
                        .border(1.dp, if (isSelected) GoldPrimary else BorderSubtle, PillShape)
                        .clickable { viewModel.setFilterType(type) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BgPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (state.selectedTab == LibraryTab.BOOKMARKS) {
                if (filteredBookmarks.isEmpty()) {
                    EmptyState(
                        title = "Belum Ada Favorit",
                        description = "Simpan anime atau manga favorit Anda agar mudah ditemukan kembali.",
                        icon = Icons.Outlined.BookmarkBorder,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredBookmarks, key = { it.id }) { item ->
                            BookmarkRowItem(
                                item = item,
                                onClick = {
                                    if (item.mediaType == MediaType.ANIME) {
                                        onNavigateToAnimeDetail(item.id)
                                    } else {
                                        onNavigateToMangaDetail(item.id)
                                    }
                                },
                                onDelete = { viewModel.removeBookmark(item.id) }
                            )
                        }
                    }
                }
            } else {
                if (filteredHistory.isEmpty()) {
                    EmptyState(
                        title = "Riwayat Kosong",
                        description = "Tonton anime atau baca komik untuk mencatat riwayat progres Anda.",
                        icon = Icons.Outlined.History,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredHistory, key = { it.id }) { item ->
                            HistoryRowItem(
                                item = item,
                                onClick = {
                                    if (item.mediaType == MediaType.ANIME) {
                                        onNavigateToVideoPlayer(item.lastItemId, item.id, item.lastItemTitle)
                                    } else {
                                        onNavigateToMangaReader(item.lastItemId, item.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkRowItem(
    item: BookmarkItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(BgCard)
            .border(1.dp, BorderSubtle, CardShape)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(52.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(BgPrimary)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(Color(0x22D4AF37))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (item.mediaType == MediaType.ANIME) "Anime" else "Manga",
                    color = GoldPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!item.extraInfo.isNullOrBlank()) {
                Text(
                    text = item.extraInfo,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Hapus",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun HistoryRowItem(
    item: HistoryItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(BgCard)
            .border(1.dp, BorderSubtle, CardShape)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(52.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(BgPrimary)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(Color(0x22D4AF37))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (item.mediaType == MediaType.ANIME) "Anime" else "Manga",
                    color = GoldPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Terakhir: ${item.lastItemTitle}",
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (item.totalProgress > 0) {
                val progressFraction = (item.progress.toFloat() / item.totalProgress.toFloat()).coerceIn(0f, 1f)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = GoldPrimary,
                    trackColor = Color(0xFF252832)
                )
            }
        }
    }
}

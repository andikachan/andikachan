package com.ndichan.app.presentation.anime.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.ndichan.app.core.components.ErrorView
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.BottomSheetShape
import com.ndichan.app.core.theme.CardShape
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import com.ndichan.app.domain.model.StreamServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNextEpisode: (String, String, String) -> Unit,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val player = viewModel.getPlayer()

    BackHandler {
        viewModel.saveHistory()
        onNavigateBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveHistory()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (state.errorMessage != null && state.episodeStream == null) {
            ErrorView(
                message = state.errorMessage ?: "Gagal memuat video",
                onRetry = { viewModel.loadEpisodeStream(viewModel.episodeId) },
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Android View for Media3 PlayerView
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Custom Controls Overlay
            PlayerControlsOverlay(
                isVisible = state.isControlsVisible,
                isPlaying = state.isPlaying,
                isBuffering = state.isBuffering,
                currentPositionMs = state.currentPositionMs,
                durationMs = state.durationMs,
                episodeTitle = state.episodeStream?.currentEpisode?.title ?: viewModel.episodeTitle,
                currentQuality = state.selectedServer?.quality ?: "Auto",
                hasNextEpisode = state.episodeStream?.nextEpisode != null,
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSeekTo = { viewModel.seekTo(it) },
                onReplay10 = { viewModel.replay10() },
                onForward10 = { viewModel.forward10() },
                onNextEpisode = {
                    state.episodeStream?.nextEpisode?.let { next ->
                        onNavigateToNextEpisode(next.id, viewModel.animeId, next.title)
                    }
                },
                onQualityClick = { viewModel.setQualitySheetVisible(true) },
                onBackClick = {
                    viewModel.saveHistory()
                    onNavigateBack()
                },
                onOverlayClick = { viewModel.toggleControls() }
            )
        }

        // Quality and Server Selection BottomSheet
        if (state.showQualitySheet) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { viewModel.setQualitySheetVisible(false) },
                sheetState = sheetState,
                containerColor = BgCard,
                shape = BottomSheetShape
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Pilih Server & Resolusi",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val servers = state.episodeStream?.servers ?: emptyList()
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        items(servers) { server ->
                            val isSelected = state.selectedServer?.id == server.id
                            ServerSelectionItem(
                                server = server,
                                isSelected = isSelected,
                                onClick = { viewModel.selectServer(server) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerSelectionItem(
    server: StreamServer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (isSelected) Color(0x33D4AF37) else BgPrimary)
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${server.name} - ${server.quality}",
                color = if (isSelected) GoldPrimary else TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = "Tipe: ${server.type}",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

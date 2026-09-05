package com.ndichan.app.presentation.anime.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.EpisodeStream
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType
import com.ndichan.app.domain.model.StreamServer
import com.ndichan.app.domain.repository.AnimeRepository
import com.ndichan.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoPlayerUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val episodeStream: EpisodeStream? = null,
    val selectedServer: StreamServer? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isControlsVisible: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val showQualitySheet: Boolean = false
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val animeRepository: AnimeRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var episodeId: String = checkNotNull(savedStateHandle["episodeId"])
        private set

    var animeId: String = savedStateHandle["animeId"] ?: ""
        private set

    var episodeTitle: String = savedStateHandle["episodeTitle"] ?: "Episode"
        private set

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var progressTrackingJob: Job? = null
    private var controlsHideJob: Job? = null

    init {
        initPlayer()
        loadEpisodeStream(episodeId)
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> _uiState.update { it.copy(isBuffering = true) }
                        Player.STATE_READY -> {
                            _uiState.update {
                                it.copy(
                                    isBuffering = false,
                                    durationMs = duration.coerceAtLeast(0L)
                                )
                            }
                        }
                        Player.STATE_ENDED -> {
                            _uiState.update { it.copy(isPlaying = false) }
                            saveHistory()
                        }
                        Player.STATE_IDLE -> _uiState.update { it.copy(isBuffering = false) }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _uiState.update { it.copy(isPlaying = isPlaying) }
                    if (isPlaying) {
                        startProgressTracker()
                        scheduleHideControls()
                    } else {
                        stopProgressTracker()
                    }
                }
            })
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun loadEpisodeStream(id: String) {
        episodeId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            animeRepository.getEpisodeStream(id).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val stream = res.data
                        val defaultServer = stream?.servers?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                episodeStream = stream,
                                selectedServer = defaultServer,
                                errorMessage = null
                            )
                        }
                        if (defaultServer != null) {
                            playVideo(defaultServer.link)
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

    fun selectServer(server: StreamServer) {
        _uiState.update { it.copy(selectedServer = server, showQualitySheet = false) }
        val currentPos = exoPlayer?.currentPosition ?: 0L
        playVideo(server.link, resumePosition = currentPos)
    }

    private fun playVideo(url: String, resumePosition: Long = 0L) {
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            if (resumePosition > 0L) {
                player.seekTo(resumePosition)
            }
            player.playWhenReady = true
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _uiState.update { it.copy(currentPositionMs = positionMs) }
        scheduleHideControls()
    }

    fun replay10() {
        val current = exoPlayer?.currentPosition ?: 0L
        seekTo((current - 10000L).coerceAtLeast(0L))
    }

    fun forward10() {
        val current = exoPlayer?.currentPosition ?: 0L
        val maxDuration = exoPlayer?.duration ?: 0L
        seekTo((current + 10000L).coerceAtMost(maxDuration))
    }

    fun toggleControls() {
        val willShow = !_uiState.value.isControlsVisible
        _uiState.update { it.copy(isControlsVisible = willShow) }
        if (willShow && _uiState.value.isPlaying) {
            scheduleHideControls()
        }
    }

    fun setQualitySheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showQualitySheet = visible) }
    }

    private fun scheduleHideControls() {
        controlsHideJob?.cancel()
        controlsHideJob = viewModelScope.launch {
            delay(4000L)
            _uiState.update { it.copy(isControlsVisible = false) }
        }
    }

    private fun startProgressTracker() {
        progressTrackingJob?.cancel()
        progressTrackingJob = viewModelScope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    val pos = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration.coerceAtLeast(0L)
                    _uiState.update { it.copy(currentPositionMs = pos, durationMs = dur) }
                }
                delay(1000L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackingJob?.cancel()
    }

    fun saveHistory() {
        val stream = _uiState.value.episodeStream ?: return
        val currentEp = stream.currentEpisode
        val pos = _uiState.value.currentPositionMs
        val dur = _uiState.value.durationMs

        viewModelScope.launch {
            libraryRepository.saveHistory(
                HistoryItem(
                    id = animeId.ifBlank { currentEp.movieId ?: episodeId },
                    mediaType = MediaType.ANIME,
                    title = currentEp.title,
                    coverUrl = currentEp.imageUrl,
                    lastItemTitle = currentEp.title,
                    lastItemId = currentEp.id,
                    progress = pos,
                    totalProgress = dur,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveHistory()
        exoPlayer?.release()
        exoPlayer = null
    }
}

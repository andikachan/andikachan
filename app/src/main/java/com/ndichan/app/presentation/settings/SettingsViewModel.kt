package com.ndichan.app.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val autoPlayNext: Boolean = true,
    val defaultQuality: String = "720p",
    val webtoonMode: Boolean = true,
    val cacheSizeBytes: Long = 0L,
    val isCacheCleared: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        calculateCacheSize()
    }

    fun toggleAutoPlayNext() {
        _uiState.update { it.copy(autoPlayNext = !it.autoPlayNext) }
    }

    fun setDefaultQuality(quality: String) {
        _uiState.update { it.copy(defaultQuality = quality) }
    }

    fun toggleWebtoonMode() {
        _uiState.update { it.copy(webtoonMode = !it.webtoonMode) }
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.cacheDir.deleteRecursively()
                context.codeCacheDir.deleteRecursively()
            } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(cacheSizeBytes = 0L, isCacheCleared = true) }
            }
        }
    }

    private fun calculateCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val size = getDirSize(context.cacheDir)
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(cacheSizeBytes = size) }
            }
        }
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        val files = dir.listFiles() ?: return 0L
        for (f in files) {
            size += if (f.isDirectory) getDirSize(f) else f.length()
        }
        return size
    }
}

package com.ndichan.app.domain.model

enum class MediaType {
    ANIME,
    MANGA
}

data class BookmarkItem(
    val id: String, // anime id or manga slug
    val mediaType: MediaType,
    val title: String,
    val coverUrl: String?,
    val extraInfo: String?, // e.g. "Ongoing", "Chapter 623", "12 Episodes"
    val timestamp: Long
)

data class HistoryItem(
    val id: String, // anime id or manga slug
    val mediaType: MediaType,
    val title: String,
    val coverUrl: String?,
    val lastItemTitle: String, // e.g. "Episode 12" or "Chapter 623"
    val lastItemId: String, // episode id or chapter slug
    val progress: Long, // video position in ms or manga page index
    val totalProgress: Long, // video duration in ms or manga total pages
    val timestamp: Long
)

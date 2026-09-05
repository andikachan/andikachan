package com.ndichan.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ndichan.app.domain.model.HistoryItem
import com.ndichan.app.domain.model.MediaType

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val id: String, // anime id or manga slug
    val mediaType: String, // "ANIME" or "MANGA"
    val title: String,
    val coverUrl: String?,
    val lastItemTitle: String,
    val lastItemId: String,
    val progress: Long,
    val totalProgress: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): HistoryItem = HistoryItem(
        id = id,
        mediaType = if (mediaType == "ANIME") MediaType.ANIME else MediaType.MANGA,
        title = title,
        coverUrl = coverUrl,
        lastItemTitle = lastItemTitle,
        lastItemId = lastItemId,
        progress = progress,
        totalProgress = totalProgress,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(item: HistoryItem): HistoryEntity = HistoryEntity(
            id = item.id,
            mediaType = item.mediaType.name,
            title = item.title,
            coverUrl = item.coverUrl,
            lastItemTitle = item.lastItemTitle,
            lastItemId = item.lastItemId,
            progress = item.progress,
            totalProgress = item.totalProgress,
            timestamp = item.timestamp
        )
    }
}

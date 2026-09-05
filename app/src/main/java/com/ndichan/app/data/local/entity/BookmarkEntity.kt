package com.ndichan.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ndichan.app.domain.model.BookmarkItem
import com.ndichan.app.domain.model.MediaType

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String, // anime id or manga slug
    val mediaType: String, // "ANIME" or "MANGA"
    val title: String,
    val coverUrl: String?,
    val extraInfo: String?,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): BookmarkItem = BookmarkItem(
        id = id,
        mediaType = if (mediaType == "ANIME") MediaType.ANIME else MediaType.MANGA,
        title = title,
        coverUrl = coverUrl,
        extraInfo = extraInfo,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(item: BookmarkItem): BookmarkEntity = BookmarkEntity(
            id = item.id,
            mediaType = item.mediaType.name,
            title = item.title,
            coverUrl = item.coverUrl,
            extraInfo = item.extraInfo,
            timestamp = item.timestamp
        )
    }
}

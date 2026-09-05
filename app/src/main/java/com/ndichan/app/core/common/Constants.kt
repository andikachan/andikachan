package com.ndichan.app.core.common

object Constants {
    const val BASE_URL = "https://api.ndikacunk.my.id/"
    const val DATABASE_NAME = "ndichan_database"
    const val HTTP_TIMEOUT_SECONDS = 30L

    // Cache TTL
    const val CACHE_MAX_SIZE_BYTES = 50L * 1024L * 1024L // 50 MB

    // Player defaults
    const val SEEK_INTERVAL_MS = 10000L // 10 seconds seek
    const val AUTO_HIDE_CONTROLS_DELAY_MS = 4000L

    // Manga defaults
    const val DEFAULT_MANGA_LIMIT = 20
    const val POPULAR_MANGA_LIMIT = 15
    const val LATEST_MANGA_LIMIT = 24
}

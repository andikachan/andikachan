package com.ndichan.app.domain.model

data class Anime(
    val id: String,
    val title: String,
    val synopsis: String,
    val posterUrl: String?,
    val coverUrl: String?,
    val type: String?,
    val year: String?,
    val day: String?,
    val status: String?,
    val views: String?,
    val favorites: String?,
    val genre: String?,
    val latestEpisode: String? = null,
    val latestEpisodeTitle: String? = null,
    val latestEpisodeId: String? = null
)

data class AnimeGenre(
    val id: String,
    val name: String,
    val group: String?
)

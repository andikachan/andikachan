package com.ndichan.app.domain.model

data class AnimeDetail(
    val id: String,
    val title: String,
    val synopsis: String,
    val synonyms: String?,
    val posterUrl: String?,
    val coverUrl: String?,
    val type: String?,
    val year: String?,
    val day: String?,
    val status: String?,
    val views: String?,
    val favorites: String?,
    val studio: String?,
    val airedStart: String?,
    val airedEnd: String?,
    val genre: String?,
    val isFavorite: Boolean,
    val episodes: List<Episode>
)

data class Episode(
    val id: String,
    val index: String,
    val title: String,
    val views: String?,
    val movieId: String?,
    val releaseDate: String?,
    val imageUrl: String?,
    val isNew: Boolean
)

data class StreamServer(
    val id: String,
    val link: String,
    val quality: String,
    val name: String,
    val type: String
)

data class EpisodeStream(
    val currentEpisode: Episode,
    val servers: List<StreamServer>,
    val nextEpisode: Episode?
)

data class ScheduleDay(
    val dayName: String,
    val animes: List<Anime>
)

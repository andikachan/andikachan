package com.ndichan.app.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class AnimeBaseResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("hasMore") val hasMore: Boolean? = null
)

data class GenreDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("group") val group: String? = null,
    @SerializedName("choseExplore") val choseExplore: Boolean? = null
)

data class MovieDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("synonyms") val synonyms: String? = null,
    @SerializedName("image_poster") val imagePoster: String? = null,
    @SerializedName("image cover") val imageCoverAlt: String? = null,
    @SerializedName("image_cover") val imageCover: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("day") val day: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("favorites") val favorites: String? = null,
    @SerializedName("views") val views: String? = null,
    @SerializedName("genre") val genre: JsonElement? = null,
    @SerializedName("aired_start") val airedStart: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("key_time") val keyTime: String? = null,
    @SerializedName("key_status") val keyStatus: String? = null,
    @SerializedName("episode") val episode: String? = null,
    @SerializedName("episode_title") val episodeTitle: String? = null,
    @SerializedName("episode_id") val episodeId: String? = null
) {
    val displayPoster: String?
        get() = imagePoster ?: imageCover ?: imageCoverAlt

    val displayGenre: String?
        get() = genre.toGenreString()
}

data class AnimeDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("synonyms") val synonyms: String? = null,
    @SerializedName("image_poster") val imagePoster: String? = null,
    @SerializedName("image_cover") val imageCover: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("day") val day: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("views") val views: String? = null,
    @SerializedName("favorites") val favorites: String? = null,
    @SerializedName("studio") val studio: String? = null,
    @SerializedName("aired_start") val airedStart: String? = null,
    @SerializedName("aired_end") val airedEnd: String? = null,
    @SerializedName("key_status") val keyStatus: String? = null,
    @SerializedName("genre") val genre: JsonElement? = null,
    @SerializedName("is_fav") val isFav: String? = null,
    @SerializedName("episode_list_complete") val episodeListComplete: Boolean? = null,
    @SerializedName("episode_list") val episodeList: List<EpisodeItemDto>? = null
) {
    val displayGenre: String?
        get() = genre.toGenreString()
}

data class EpisodeItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("index") val index: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("views") val views: String? = null,
    @SerializedName("id_movie") val idMovie: String? = null,
    @SerializedName("key_time") val keyTime: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("is_new") val isNew: String? = null,
    @SerializedName("is_book") val isBook: Any? = null
)

data class EpisodeStreamDto(
    @SerializedName("episode") val episode: EpisodeItemDto? = null,
    @SerializedName("server") val server: List<ServerItemDto>? = null,
    @SerializedName("next_episode") val nextEpisode: EpisodeItemDto? = null
)

data class ServerItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("link") val link: String,
    @SerializedName("quality") val quality: String? = null,
    @SerializedName("key_file_size") val keyFileSize: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("domain") val domain: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("server_id") val serverId: String? = null
)

data class ScheduleResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: Map<String, List<MovieDto>>? = null
)

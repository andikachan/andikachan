package com.ndichan.app.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

fun JsonElement?.toGenreList(): List<String> {
    if (this == null || this.isJsonNull) return emptyList()
    return try {
        if (this.isJsonArray) {
            val list = mutableListOf<String>()
            this.asJsonArray.forEach { elem ->
                if (!elem.isJsonNull) {
                    list.add(elem.asString)
                }
            }
            list
        } else if (this.isJsonPrimitive) {
            val str = this.asString
            if (str.contains(",")) {
                str.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            } else if (str.isNotBlank()) {
                listOf(str.trim())
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}

fun JsonElement?.toGenreString(): String? {
    val list = this.toGenreList()
    return if (list.isEmpty()) null else list.joinToString(", ")
}

data class MangaListResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<MangaItemDto>? = null,
    @SerializedName("cursor") val cursor: MangaCursorDto? = null
)

data class MangaCursorDto(
    @SerializedName("hasNext") val hasNext: Boolean = false,
    @SerializedName("hasPrev") val hasPrev: Boolean = false,
    @SerializedName("nextCursor") val nextCursor: String? = null,
    @SerializedName("prevCursor") val prevCursor: String? = null
)

data class MangaItemDto(
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("big_cover") val bigCover: String? = null,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("genre") val genre: JsonElement? = null,
    @SerializedName("genres") val genres: JsonElement? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("sinopsis") val sinopsis: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("is_project") val isProject: Boolean? = null,
    @SerializedName("chapters") val chapters: List<MangaChapterDto>? = null
) {
    val displayCover: String?
        get() = cover ?: bigCover

    val parsedGenres: List<String>
        get() = genres.toGenreList().ifEmpty { genre.toGenreList() }

    val parsedGenreString: String?
        get() = genre.toGenreString() ?: genres.toGenreString()

    val displaySummary: String?
        get() = summary ?: sinopsis
}

data class MangaChapterDto(
    @SerializedName("chapterNum") val chapterNum: String? = null,
    @SerializedName("chapter") val chapter: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("redirect_link") val redirectLink: String? = null
) {
    val displayChapterNum: String
        get() = chapterNum ?: chapter ?: "0"

    val displaySlug: String
        get() = slug ?: ""
}

data class MangaDetailResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: MangaDetailDto? = null,
    @SerializedName("related") val related: List<MangaItemDto>? = null
)

data class MangaDetailDto(
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("big_cover") val bigCover: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("sinopsis") val sinopsis: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("genre") val genre: JsonElement? = null,
    @SerializedName("genres") val genres: JsonElement? = null,
    @SerializedName("chapters") val chapters: List<MangaChapterDto>? = null,
    @SerializedName("chapterList") val chapterList: List<MangaChapterDto>? = null
) {
    val allChapters: List<MangaChapterDto>
        get() = chapters ?: chapterList ?: emptyList()

    val displaySynopsis: String?
        get() = synopsis ?: sinopsis ?: description

    val parsedGenres: List<String>
        get() = genres.toGenreList().ifEmpty { genre.toGenreList() }
}

data class MangaReadingResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: MangaReadingDto? = null
)

data class MangaReadingDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("chapter_title") val chapterTitle: String? = null,
    @SerializedName("chapter") val chapter: String? = null,
    @SerializedName("slug_manga") val slugManga: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("redirect_link") val redirectLink: String? = null,
    @SerializedName("background_music_url") val backgroundMusicUrl: String? = null,
    @SerializedName("pages") val pages: List<String>? = null,
    @SerializedName("other_chapters") val otherChapters: List<MangaChapterDto>? = null
)

package com.ndichan.app.data.remote.dto

import com.google.gson.annotations.SerializedName

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
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("is_project") val isProject: Boolean? = null,
    @SerializedName("chapters") val chapters: List<MangaChapterDto>? = null
) {
    val displayCover: String?
        get() = cover ?: bigCover
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
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("artist") val artist: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("chapters") val chapters: List<MangaChapterDto>? = null,
    @SerializedName("chapterList") val chapterList: List<MangaChapterDto>? = null
) {
    val allChapters: List<MangaChapterDto>
        get() = chapters ?: chapterList ?: emptyList()

    val displaySynopsis: String?
        get() = synopsis ?: description
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

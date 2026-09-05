package com.ndichan.app.domain.model

data class Manga(
    val title: String,
    val slug: String,
    val coverUrl: String?,
    val badge: String?,
    val genre: String?,
    val genres: List<String>,
    val author: String?,
    val summary: String?,
    val rating: String?,
    val status: String?,
    val type: String?,
    val isProject: Boolean,
    val latestChapter: String?,
    val latestChapterSlug: String?
)

data class MangaChapter(
    val chapterNum: String,
    val slug: String,
    val time: String?,
    val cover: String?
)

data class MangaDetail(
    val title: String,
    val slug: String,
    val coverUrl: String?,
    val synopsis: String,
    val author: String?,
    val artist: String?,
    val rating: String?,
    val status: String?,
    val type: String?,
    val genres: List<String>,
    val chapters: List<MangaChapter>,
    val relatedManga: List<Manga>
)

data class MangaReading(
    val title: String,
    val chapterTitle: String,
    val chapterNum: String,
    val mangaSlug: String,
    val time: String?,
    val backgroundMusicUrl: String?,
    val pages: List<String>,
    val otherChapters: List<MangaChapter>
)

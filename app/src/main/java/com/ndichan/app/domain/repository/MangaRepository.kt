package com.ndichan.app.domain.repository

import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.Manga
import com.ndichan.app.domain.model.MangaDetail
import com.ndichan.app.domain.model.MangaReading
import kotlinx.coroutines.flow.Flow

interface MangaRepository {
    suspend fun getHeroSlider(limit: Int = 5): Flow<Resource<List<Manga>>>
    suspend fun getPopularToday(limit: Int = 15): Flow<Resource<List<Manga>>>
    suspend fun getLatest(limit: Int = 42): Flow<Resource<List<Manga>>>
    suspend fun getLatestProject(limit: Int = 18): Flow<Resource<List<Manga>>>
    suspend fun getAllComics(limit: Int = 20, after: String? = null): Flow<Resource<List<Manga>>>
    suspend fun getMangaDetail(slug: String): Flow<Resource<MangaDetail>>
    suspend fun getMangaReading(chapterSlug: String): Flow<Resource<MangaReading>>
    suspend fun searchManga(query: String): Flow<Resource<List<Manga>>>
    suspend fun filterManga(
        genreSlug: String? = null,
        status: String? = null,
        type: String? = null,
        orderBy: String = "popular",
        limit: Int = 20,
        after: String? = null
    ): Flow<Resource<List<Manga>>>
}

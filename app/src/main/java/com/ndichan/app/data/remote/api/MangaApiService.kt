package com.ndichan.app.data.remote.api

import com.ndichan.app.data.remote.dto.MangaDetailResponseDto
import com.ndichan.app.data.remote.dto.MangaListResponseDto
import com.ndichan.app.data.remote.dto.MangaReadingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MangaApiService {

    @GET("v1/manga/heroslider")
    suspend fun getHeroSlider(
        @Query("limit") limit: Int = 5,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/populartoday")
    suspend fun getPopularToday(
        @Query("limit") limit: Int = 15,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/latest")
    suspend fun getLatest(
        @Query("limit") limit: Int = 42,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/latestproject")
    suspend fun getLatestProject(
        @Query("limit") limit: Int = 18,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/allcomics")
    suspend fun getAllComics(
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/detail")
    suspend fun getMangaDetail(
        @Query("slug") slug: String,
        @Query("limit") limit: Int = 10
    ): MangaDetailResponseDto

    @GET("v1/manga/read")
    suspend fun getMangaReading(
        @Query("slug") chapterSlug: String
    ): MangaReadingResponseDto

    @GET("v1/manga/search")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("genres") genres: String? = null
    ): MangaListResponseDto

    @GET("v1/manga/filter")
    suspend fun filterManga(
        @Query("genres_slug") genreSlug: String? = null,
        @Query("release_status") releaseStatus: String? = null,
        @Query("type_manga") typeManga: String? = null,
        @Query("order_by") orderBy: String = "az",
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null
    ): MangaListResponseDto
}

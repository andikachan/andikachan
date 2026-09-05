package com.ndichan.app.data.remote.api

import com.ndichan.app.data.remote.dto.AnimeBaseResponse
import com.ndichan.app.data.remote.dto.AnimeDetailDto
import com.ndichan.app.data.remote.dto.EpisodeStreamDto
import com.ndichan.app.data.remote.dto.GenreDto
import com.ndichan.app.data.remote.dto.MovieDto
import com.ndichan.app.data.remote.dto.ScheduleResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApiService {

    @GET("v1/genre")
    suspend fun getGenres(): AnimeBaseResponse<List<GenreDto>>

    @GET("v1/genre")
    suspend fun getAnimeByGenre(
        @Query("id") genreId: String,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int? = null
    ): AnimeBaseResponse<List<MovieDto>>

    @GET("v1/popular")
    suspend fun getPopularAnime(
        @Query("page") page: Int = 1
    ): AnimeBaseResponse<List<MovieDto>>

    @GET("v1/new")
    suspend fun getNewEpisodes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 27
    ): AnimeBaseResponse<List<MovieDto>>

    @GET("v1/schedule")
    suspend fun getSchedule(): ScheduleResponseDto

    @GET("v1/ongoing")
    suspend fun getOngoingAnime(): AnimeBaseResponse<List<MovieDto>>

    @GET("v1/detail")
    suspend fun getAnimeDetail(
        @Query("id") id: String
    ): AnimeBaseResponse<AnimeDetailDto>

    @GET("v1/episode")
    suspend fun getEpisodeStream(
        @Query("id") episodeId: String
    ): AnimeBaseResponse<EpisodeStreamDto>

    @GET("v1/search")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("genre") genre: String? = null,
        @Query("sort") sort: String? = "views",
        @Query("page") page: Int? = 0
    ): AnimeBaseResponse<List<MovieDto>>
}

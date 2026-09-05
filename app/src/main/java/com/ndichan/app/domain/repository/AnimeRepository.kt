package com.ndichan.app.domain.repository

import com.ndichan.app.core.common.Resource
import com.ndichan.app.domain.model.Anime
import com.ndichan.app.domain.model.AnimeDetail
import com.ndichan.app.domain.model.AnimeGenre
import com.ndichan.app.domain.model.EpisodeStream
import com.ndichan.app.domain.model.ScheduleDay
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    suspend fun getGenres(): Flow<Resource<List<AnimeGenre>>>
    suspend fun getAnimeByGenre(genreId: String, page: Int = 1): Flow<Resource<List<Anime>>>
    suspend fun getPopularAnime(page: Int = 1): Flow<Resource<List<Anime>>>
    suspend fun getNewEpisodes(page: Int = 1, limit: Int = 27): Flow<Resource<List<Anime>>>
    suspend fun getSchedule(): Flow<Resource<List<ScheduleDay>>>
    suspend fun getOngoingAnime(): Flow<Resource<List<Anime>>>
    suspend fun getAnimeDetail(id: String): Flow<Resource<AnimeDetail>>
    suspend fun getEpisodeStream(episodeId: String): Flow<Resource<EpisodeStream>>
    suspend fun searchAnime(query: String, page: Int = 0): Flow<Resource<List<Anime>>>
}

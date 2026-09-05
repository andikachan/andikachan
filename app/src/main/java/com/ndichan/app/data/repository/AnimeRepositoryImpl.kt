package com.ndichan.app.data.repository

import com.ndichan.app.core.common.Constants
import com.ndichan.app.core.common.Resource
import com.ndichan.app.data.remote.api.AnimeApiService
import com.ndichan.app.data.remote.dto.MovieDto
import com.ndichan.app.domain.model.Anime
import com.ndichan.app.domain.model.AnimeDetail
import com.ndichan.app.domain.model.AnimeGenre
import com.ndichan.app.domain.model.Episode
import com.ndichan.app.domain.model.EpisodeStream
import com.ndichan.app.domain.model.ScheduleDay
import com.ndichan.app.domain.model.StreamServer
import com.ndichan.app.domain.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
) : AnimeRepository {

    private fun MovieDto.toDomain(): Anime {
        return Anime(
            id = id,
            title = title,
            synopsis = synopsis ?: "",
            posterUrl = Constants.formatAnimeImageUrl(displayPoster),
            coverUrl = Constants.formatAnimeImageUrl(imageCover ?: imageCoverAlt ?: imagePoster),
            type = type,
            year = year,
            day = day,
            status = status,
            views = views,
            favorites = favorites,
            genre = displayGenre,
            latestEpisode = episode,
            latestEpisodeTitle = episodeTitle,
            latestEpisodeId = episodeId
        )
    }

    override suspend fun getGenres(): Flow<Resource<List<AnimeGenre>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getGenres()
            if (response.status && response.data != null) {
                val genres = response.data.map {
                    AnimeGenre(
                        id = it.id,
                        name = it.name,
                        group = it.group
                    )
                }
                emit(Resource.Success(genres))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat genre anime"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAnimeByGenre(genreId: String, page: Int): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAnimeByGenre(genreId = genreId, page = page)
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat anime"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getPopularAnime(page: Int): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getPopularAnime(page = page)
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat anime populer"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getNewEpisodes(page: Int, limit: Int): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNewEpisodes(page = page, limit = limit)
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat episode terbaru"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getSchedule(): Flow<Resource<List<ScheduleDay>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSchedule()
            if (response.status && response.data != null) {
                val dayOrder = listOf("SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "MINGGU")
                val scheduleList = dayOrder.mapNotNull { dayKey ->
                    val animes = response.data[dayKey]
                    if (animes != null) {
                        ScheduleDay(
                            dayName = dayKey,
                            animes = animes.map { it.toDomain() }
                        )
                    } else null
                }
                emit(Resource.Success(scheduleList))
            } else {
                emit(Resource.Error("Gagal memuat jadwal tayang"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getOngoingAnime(): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOngoingAnime()
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat anime ongoing"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAnimeDetail(id: String): Flow<Resource<AnimeDetail>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAnimeDetail(id)
            if (response.status && response.data != null) {
                val d = response.data
                val episodes = (d.episodeList ?: emptyList()).map { ep ->
                    Episode(
                        id = ep.id,
                        index = ep.index ?: "1",
                        title = ep.title,
                        views = ep.views,
                        movieId = ep.idMovie ?: d.id,
                        releaseDate = ep.keyTime,
                        imageUrl = Constants.formatAnimeImageUrl(ep.image),
                        isNew = ep.isNew == "1"
                    )
                }

                val detail = AnimeDetail(
                    id = d.id,
                    title = d.title,
                    synopsis = d.synopsis ?: "",
                    synonyms = d.synonyms,
                    posterUrl = Constants.formatAnimeImageUrl(d.imagePoster),
                    coverUrl = Constants.formatAnimeImageUrl(d.imageCover ?: d.imagePoster),
                    type = d.type,
                    year = d.year,
                    day = d.day,
                    status = d.status,
                    views = d.views,
                    favorites = d.favorites,
                    studio = d.studio,
                    airedStart = d.airedStart,
                    airedEnd = d.airedEnd,
                    genre = d.displayGenre,
                    isFavorite = d.isFav == "1",
                    episodes = episodes
                )
                emit(Resource.Success(detail))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat detail anime"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getEpisodeStream(episodeId: String): Flow<Resource<EpisodeStream>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getEpisodeStream(episodeId)
            if (response.status && response.data != null) {
                val d = response.data
                val currentEp = Episode(
                    id = d.episode?.id ?: episodeId,
                    index = d.episode?.index ?: "1",
                    title = d.episode?.title ?: "Episode",
                    views = d.episode?.views,
                    movieId = d.episode?.idMovie,
                    releaseDate = d.episode?.keyTime,
                    imageUrl = Constants.formatAnimeImageUrl(d.episode?.image),
                    isNew = false
                )

                val servers = (d.server ?: emptyList()).map { s ->
                    StreamServer(
                        id = s.id,
                        link = Constants.formatAnimeStreamUrl(s.link),
                        quality = s.quality ?: "Auto",
                        name = s.name ?: "Default Server",
                        type = s.type ?: "direct"
                    )
                }

                val nextEp = d.nextEpisode?.let { n ->
                    Episode(
                        id = n.id,
                        index = n.index ?: "",
                        title = n.title,
                        views = n.views,
                        movieId = n.idMovie,
                        releaseDate = n.keyTime,
                        imageUrl = Constants.formatAnimeImageUrl(n.image),
                        isNew = false
                    )
                }

                emit(Resource.Success(EpisodeStream(currentEpisode = currentEp, servers = servers, nextEpisode = nextEp)))
            } else {
                emit(Resource.Error(response.message ?: "Gagal memuat stream video"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun searchAnime(query: String, page: Int): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchAnime(query = query, page = page)
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message ?: "Gagal mencari anime"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)
}

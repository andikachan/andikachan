package com.ndichan.app.data.repository

import com.ndichan.app.core.common.Resource
import com.ndichan.app.data.remote.api.MangaApiService
import com.ndichan.app.data.remote.dto.MangaChapterDto
import com.ndichan.app.data.remote.dto.MangaItemDto
import com.ndichan.app.domain.model.Manga
import com.ndichan.app.domain.model.MangaChapter
import com.ndichan.app.domain.model.MangaDetail
import com.ndichan.app.domain.model.MangaReading
import com.ndichan.app.domain.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val apiService: MangaApiService
) : MangaRepository {

    private fun MangaItemDto.toDomain(): Manga {
        val lastCh = chapters?.firstOrNull()
        return Manga(
            title = title,
            slug = slug,
            coverUrl = displayCover,
            badge = badge,
            genre = parsedGenreString,
            genres = parsedGenres,
            author = author,
            summary = displaySummary,
            rating = rating,
            status = status,
            type = type,
            isProject = isProject == true,
            latestChapter = lastCh?.displayChapterNum,
            latestChapterSlug = lastCh?.displaySlug
        )
    }

    private fun MangaChapterDto.toDomain(): MangaChapter {
        return MangaChapter(
            chapterNum = displayChapterNum,
            slug = displaySlug,
            time = time,
            cover = cover
        )
    }

    override suspend fun getHeroSlider(limit: Int): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getHeroSlider(limit = limit)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat slider manga"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getPopularToday(limit: Int): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getPopularToday(limit = limit)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat manga populer hari ini"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getLatest(limit: Int): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getLatest(limit = limit)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat rilis manga terbaru"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getLatestProject(limit: Int): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getLatestProject(limit = limit)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat manga project"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllComics(limit: Int, after: String?): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getAllComics(limit = limit, after = after)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat daftar komik"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getMangaDetail(slug: String): Flow<Resource<MangaDetail>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getMangaDetail(slug = slug)
            if (res.success && res.data != null) {
                val d = res.data
                val chapters = d.allChapters.map { it.toDomain() }
                val related = (res.related ?: emptyList()).map { it.toDomain() }

                val detail = MangaDetail(
                    title = d.title,
                    slug = d.slug,
                    coverUrl = d.cover ?: d.bigCover ?: d.banner,
                    synopsis = d.displaySynopsis ?: "",
                    author = d.author,
                    artist = d.artist,
                    rating = d.rating,
                    status = d.status,
                    type = d.type,
                    genres = d.parsedGenres,
                    chapters = chapters,
                    relatedManga = related
                )
                emit(Resource.Success(detail))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat detail komik"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getMangaReading(chapterSlug: String): Flow<Resource<MangaReading>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.getMangaReading(chapterSlug = chapterSlug)
            if (res.success && res.data != null) {
                val d = res.data
                val reading = MangaReading(
                    title = d.title ?: "",
                    chapterTitle = d.chapterTitle ?: "Chapter",
                    chapterNum = d.chapter ?: "0",
                    mangaSlug = d.slugManga ?: "",
                    time = d.time,
                    backgroundMusicUrl = d.backgroundMusicUrl,
                    pages = d.pages ?: emptyList(),
                    otherChapters = (d.otherChapters ?: emptyList()).map { it.toDomain() }
                )
                emit(Resource.Success(reading))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memuat halaman baca komik"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun searchManga(query: String): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.searchManga(query = query)
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal mencari komik"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun filterManga(
        genreSlug: String?,
        status: String?,
        type: String?,
        orderBy: String,
        limit: Int,
        after: String?
    ): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val res = apiService.filterManga(
                genreSlug = genreSlug,
                releaseStatus = status,
                typeManga = type,
                orderBy = orderBy,
                limit = limit,
                after = after
            )
            if (res.success && res.data != null) {
                emit(Resource.Success(res.data.map { it.toDomain() }))
            } else {
                emit(Resource.Error(res.message ?: "Gagal memfilter komik"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan jaringan"))
        }
    }.flowOn(Dispatchers.IO)
}

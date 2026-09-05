package com.ndichan.app.di

import com.ndichan.app.data.repository.AnimeRepositoryImpl
import com.ndichan.app.data.repository.LibraryRepositoryImpl
import com.ndichan.app.data.repository.MangaRepositoryImpl
import com.ndichan.app.domain.repository.AnimeRepository
import com.ndichan.app.domain.repository.LibraryRepository
import com.ndichan.app.domain.repository.MangaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnimeRepository(
        animeRepositoryImpl: AnimeRepositoryImpl
    ): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindMangaRepository(
        mangaRepositoryImpl: MangaRepositoryImpl
    ): MangaRepository

    @Binds
    @Singleton
    abstract fun bindLibraryRepository(
        libraryRepositoryImpl: LibraryRepositoryImpl
    ): LibraryRepository
}

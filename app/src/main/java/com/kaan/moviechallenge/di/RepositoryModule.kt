package com.kaan.moviechallenge.di

import com.kaan.moviechallenge.data.remote.TmdbApiService
import com.kaan.moviechallenge.data.repository.MovieRepositoryImpl
import com.kaan.moviechallenge.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(api: TmdbApiService): MovieRepository {
        return MovieRepositoryImpl(api)
    }
}

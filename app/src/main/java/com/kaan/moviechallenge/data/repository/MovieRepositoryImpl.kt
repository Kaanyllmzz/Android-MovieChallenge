package com.kaan.moviechallenge.data.repository

import com.kaan.moviechallenge.data.model.MovieDetail
import com.kaan.moviechallenge.data.model.MovieResponse
import com.kaan.moviechallenge.data.remote.TmdbApiService
import com.kaan.moviechallenge.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : MovieRepository {

    override suspend fun getNowPlaying(page: Int): MovieResponse = api.getNowPlaying(page = page)
    override suspend fun getPopular(page: Int): MovieResponse = api.getPopular(page = page)
    override suspend fun getTopRated(page: Int): MovieResponse = api.getTopRated(page = page)
    override suspend fun getUpcoming(page: Int): MovieResponse = api.getUpcoming(page = page)

    override suspend fun searchMovies(query: String, page: Int): MovieResponse =
        api.searchMovies(query = query, page = page)

    override suspend fun getMovieDetail(movieId: Int): MovieDetail =
        api.getMovieDetail(movieId = movieId)
}

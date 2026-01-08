package com.kaan.moviechallenge.domain.repository

import com.kaan.moviechallenge.data.model.MovieDetail
import com.kaan.moviechallenge.data.model.MovieResponse

interface MovieRepository {
    suspend fun getNowPlaying(page: Int): MovieResponse
    suspend fun getPopular(page: Int): MovieResponse
    suspend fun getTopRated(page: Int): MovieResponse
    suspend fun getUpcoming(page: Int): MovieResponse

    suspend fun searchMovies(query: String, page: Int): MovieResponse

    // ✅ EKSİK OLAN PARÇA:
    suspend fun getMovieDetail(movieId: Int): MovieDetail
}
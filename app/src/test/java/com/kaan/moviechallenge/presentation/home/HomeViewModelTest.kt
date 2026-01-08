package com.kaan.moviechallenge.presentation.home

import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.data.model.MovieResponse
import com.kaan.moviechallenge.domain.repository.MovieRepository
import com.kaan.moviechallenge.presentation.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val repository: MovieRepository = mockk()
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load should update state to Success when api returns data`() = runTest {
        // --- GIVEN ---
        val fakeMovies = listOf(
            Movie(
                id = 1,
                title = "Test Movie",
                posterPath = null,
                releaseDate = "2024",
                voteAverage = 8.0,
                // ✅ EKSİK OLANLARI EKLEDİK:
                backdropPath = null,
                overview = "Test açıklaması",
                genreIds = emptyList()
            )
        )
        val fakeResponse = MovieResponse(page = 1, results = fakeMovies, totalPages = 1, totalResults = 1)

        coEvery { repository.getNowPlaying(1) } returns fakeResponse
        coEvery { repository.getPopular(1) } returns fakeResponse
        coEvery { repository.getTopRated(1) } returns fakeResponse
        coEvery { repository.getUpcoming(1) } returns fakeResponse

        // --- WHEN ---
        viewModel.load()
        advanceUntilIdle()

        // --- THEN ---
        val currentState = viewModel.state.value
        assertTrue(currentState is UiState.Success)

        val successState = currentState as UiState.Success
        assertEquals(fakeMovies, successState.data.nowPlaying)
    }

    @Test
    fun `load should update state to Error when api throws exception`() = runTest {
        // --- GIVEN ---
        val errorMessage = "İnternet yok"
        coEvery { repository.getNowPlaying(1) } throws RuntimeException(errorMessage)

        // --- WHEN ---
        viewModel.load()
        advanceUntilIdle()

        // --- THEN ---
        val currentState = viewModel.state.value
        assertTrue(currentState is UiState.Error)

        val errorState = currentState as UiState.Error
        assertEquals(errorMessage, errorState.message)
    }
}
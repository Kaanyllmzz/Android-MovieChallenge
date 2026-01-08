package com.kaan.moviechallenge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.domain.repository.MovieRepository
import com.kaan.moviechallenge.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope // ✅ BU EKLENDİ
import javax.inject.Inject

data class HomeUiData(
    val nowPlaying: List<Movie> = emptyList(),
    val popular: List<Movie> = emptyList(),
    val topRated: List<Movie> = emptyList(),
    val upcoming: List<Movie> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<HomeUiData>>(UiState.Idle)
    val state: StateFlow<UiState<HomeUiData>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                // ✅ DÜZELTME: supervisorScope eklendi.
                // Bu sayede async içindeki hatalar try-catch tarafından yakalanabilir.
                supervisorScope {
                    val now = async { repo.getNowPlaying(1).results }
                    val pop = async { repo.getPopular(1).results }
                    val top = async { repo.getTopRated(1).results }
                    val up = async { repo.getUpcoming(1).results }

                    _state.value = UiState.Success(
                        HomeUiData(
                            nowPlaying = now.await(),
                            popular = pop.await(),
                            topRated = top.await(),
                            upcoming = up.await()
                        )
                    )
                }
            } catch (e: Exception) {
                // Artık hata buraya düşecek ve uygulama çökmeyecek
                _state.value = UiState.Error(e.message ?: "Bir hata oluştu")
            }
        }
    }
}
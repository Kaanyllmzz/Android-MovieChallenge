package com.kaan.moviechallenge.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.moviechallenge.data.model.MovieDetail
import com.kaan.moviechallenge.domain.repository.MovieRepository
import com.kaan.moviechallenge.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State: Veri gelene kadar Loading, gelince Success, hata olursa Error
    private val _state = MutableStateFlow<UiState<MovieDetail>>(UiState.Idle)
    val state = _state.asStateFlow()

    init {
        // ViewModel başlar başlamaz yüklemeyi dene
        load()
    }

    fun load() {
        // 1. Navigation'dan gelen ID'yi al (AppNavHost'ta "movieId" demiştik)
        val movieId = savedStateHandle.get<Int>("movieId")

        if (movieId == null) {
            _state.value = UiState.Error("Film ID bulunamadı!")
            return
        }

        // 2. API isteğini başlat
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                // Repository suspend olduğu için direkt çağırıyoruz
                val result = repository.getMovieDetail(movieId)

                // Başarılı olursa UI'a veriyi ver
                _state.value = UiState.Success(result)
            } catch (e: Exception) {
                // Hata olursa (İnternet yoksa vs.) UI'a hatayı bildir
                _state.value = UiState.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu")
            }
        }
    }
}
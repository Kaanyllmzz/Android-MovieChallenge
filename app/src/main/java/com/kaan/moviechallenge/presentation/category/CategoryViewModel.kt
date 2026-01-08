package com.kaan.moviechallenge.presentation.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.moviechallenge.R
import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.domain.repository.MovieRepository
import com.kaan.moviechallenge.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Movie>>>(UiState.Idle)
    val state = _state.asStateFlow()

    // Navigasyondan gelen kategori türü (now_playing, popular vs.)
    val categoryType: String = savedStateHandle.get<String>("categoryType") ?: "popular"

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                // Gelen türe göre doğru repository fonksiyonunu seç
                val response = when (categoryType) {
                    "now_playing" -> repository.getNowPlaying(page = 1)
                    "popular" -> repository.getPopular(page = 1)
                    "top_rated" -> repository.getTopRated(page = 1)
                    "upcoming" -> repository.getUpcoming(page = 1)
                    else -> repository.getPopular(page = 1)
                }
                // MovieResponse içindeki sonuç listesini alıyoruz
                _state.value = UiState.Success(response.results)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Hata oluştu") // Buradaki hatayı string kaynaklarına taşımak opsiyoneldir
            }
        }
    }

    // ✅ DİL DESTEĞİ: Artık String değil, Int (R.string.xyz) döndürüyoruz
    fun getTitleResId(): Int {
        return when(categoryType) {
            "now_playing" -> R.string.now_playing
            "popular" -> R.string.popular
            "top_rated" -> R.string.top_rated
            "upcoming" -> R.string.upcoming
            else -> R.string.popular
        }
    }
}
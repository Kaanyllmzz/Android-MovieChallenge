package com.kaan.moviechallenge.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.domain.repository.MovieRepository
import com.kaan.moviechallenge.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow<UiState<List<Movie>>>(UiState.Idle)
    val state: StateFlow<UiState<List<Movie>>> = _state.asStateFlow()

    fun onQueryChange(newValue: String) {
        _query.value = newValue
    }

    fun search() {
        val q = _query.value.trim()
        if (q.isEmpty()) {
            _state.value = UiState.Error("Lütfen film adı yaz.")
            return
        }

        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val results = repo.searchMovies(query = q, page = 1).results
                if (results.isEmpty()) {
                    _state.value = UiState.Error("Sonuç bulunamadı.")
                } else {
                    _state.value = UiState.Success(results)
                }
            } catch (e: Exception) {
                _state.value = UiState.Error("Bağlantı hatası. İnternetini kontrol et.")
            }
        }
    }
}

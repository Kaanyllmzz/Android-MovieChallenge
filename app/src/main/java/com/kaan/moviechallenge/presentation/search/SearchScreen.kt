package com.kaan.moviechallenge.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kaan.moviechallenge.R
import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.presentation.UiState

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    vm: SearchViewModel = hiltViewModel()
) {
    val query = vm.query.collectAsStateWithLifecycle().value
    val state = vm.state.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Başlık
        Text(stringResource(R.string.search_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = vm::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_title)) },
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // ✅ GÜNCELLENEN KISIM: Buton Rengi (Siyah/Beyaz)
        Button(
            onClick = { vm.search() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                // Açık modda Siyah, Koyu modda Beyaz olması için 'onSurface' kullanıyoruz
                containerColor = MaterialTheme.colorScheme.onSurface,
                // Yazının okunması için zemin rengini (zıt renk) veriyoruz
                contentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(stringResource(R.string.search_title))
        }

        Spacer(Modifier.height(16.dp))

        when (val s = state) {
            UiState.Idle -> {
                Text(stringResource(R.string.search_title) + "...")
            }

            UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Text(
                    text = stringResource(R.string.error_generic) + ": " + s.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(s.data) { movie ->
                        SearchRow(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchRow(
    movie: Movie,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = movie.posterPath?.let { IMAGE_BASE_URL + it },
            contentDescription = movie.title,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(movie.title, style = MaterialTheme.typography.titleMedium)
            Text(movie.releaseDate ?: "", style = MaterialTheme.typography.bodySmall)
        }
    }
}
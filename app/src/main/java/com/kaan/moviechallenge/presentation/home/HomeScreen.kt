package com.kaan.moviechallenge.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaan.moviechallenge.R
import com.kaan.moviechallenge.data.model.Movie
import com.kaan.moviechallenge.presentation.UiState
import com.kaan.moviechallenge.presentation.components.MoviePosterCard

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onShowAll: (String) -> Unit = {},
    onProfileClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.load() }

    val uiState by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- ÜST BAŞLIK ALANI ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile_pic_desc),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )
        }

        Spacer(Modifier.height(12.dp))

        when (val s = uiState) {
            UiState.Idle -> {}

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
                    text = "${stringResource(R.string.error_generic)}: ${s.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Success -> {
                SectionRow(
                    title = stringResource(R.string.now_playing),
                    items = s.data.nowPlaying,
                    onMovieClick = onMovieClick,
                    onShowAll = { onShowAll("now_playing") }
                )
                SectionRow(
                    title = stringResource(R.string.popular),
                    items = s.data.popular,
                    onMovieClick = onMovieClick,
                    onShowAll = { onShowAll("popular") }
                )
                SectionRow(
                    title = stringResource(R.string.top_rated),
                    items = s.data.topRated,
                    onMovieClick = onMovieClick,
                    onShowAll = { onShowAll("top_rated") }
                )
                SectionRow(
                    title = stringResource(R.string.upcoming),
                    items = s.data.upcoming,
                    onMovieClick = onMovieClick,
                    onShowAll = { onShowAll("upcoming") }
                )
            }
        }
    }
}

@Composable
private fun SectionRow(
    title: String,
    items: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onShowAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)

        // ✅ GÜNCELLENEN KISIM: Renkler temaya bağlandı (Siyah/Beyaz)
        TextButton(
            onClick = onShowAll,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(stringResource(R.string.show_all))
        }
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items.take(12)) { movie ->
            MoviePosterCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }

    Spacer(Modifier.height(18.dp))
}
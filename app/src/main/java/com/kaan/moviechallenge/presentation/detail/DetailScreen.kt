package com.kaan.moviechallenge.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kaan.moviechallenge.presentation.UiState

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // ✅ FlowRow hatası için bu eklendi
@Composable
fun DetailScreen(
    movieId: Int, // ✅ AppNavHost ile uyumlu olması için bu parametre eklendi
    onBack: () -> Unit,
    vm: DetailViewModel = hiltViewModel()
) {
    // ViewModel SavedStateHandle kullanıyorsa movieId'ye burada gerek olmayabilir
    // ama load fonksiyonun parametre alıyorsa buraya vm.load(movieId) yazabilirsin.
    LaunchedEffect(Unit) {
        vm.load()
    }

    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val s = state) {
            UiState.Idle -> {}

            UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Success -> {
                val d = s.data

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = d.posterPath?.let { IMAGE_BASE_URL + it },
                        contentDescription = d.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                    )

                    Text(d.title, style = MaterialTheme.typography.headlineSmall)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text("⭐ ${"%.1f".format(d.voteAverage)}") }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("📅 ${d.releaseDate}") }
                        )
                        d.runtime?.let {
                            AssistChip(
                                onClick = {},
                                label = { Text("⏱ ${it} dk") }
                            )
                        }
                    }

                    if (d.genres.isNotEmpty()) {
                        Text("Türler", style = MaterialTheme.typography.titleMedium)
                        // ✅ Hata veren kısım düzeltildi (Annotation eklendiği için artık çalışır)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.genres.forEach { g ->
                                AssistChip(onClick = {}, label = { Text(g.name) })
                            }
                        }
                    }

                    Text("Açıklama", style = MaterialTheme.typography.titleMedium)
                    Text(d.overview, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
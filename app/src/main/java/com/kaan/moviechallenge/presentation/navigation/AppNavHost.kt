package com.kaan.moviechallenge.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaan.moviechallenge.presentation.category.CategoryScreen
import com.kaan.moviechallenge.presentation.detail.DetailScreen
import com.kaan.moviechallenge.presentation.home.HomeScreen
import com.kaan.moviechallenge.presentation.profile.ProfileScreen
import com.kaan.moviechallenge.presentation.search.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,             // ✅ YENİ: Tema bilgisi buraya geliyor
    onThemeChanged: (Boolean) -> Unit // ✅ YENİ: Değiştirme fonksiyonu buraya geliyor
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Profil ve Detay sayfalarında BottomBar gizlensin mi? Şimdilik kalsın.
            BottomBar(
                currentRoute = currentRoute,
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME) { saveState = true }
                    }
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            // --- ANA SAYFA ---
            composable(Routes.HOME) {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Routes.detail(movieId))
                    },
                    onShowAll = { key ->
                        if (key == "search") {
                            navController.navigate(Routes.SEARCH)
                        } else {
                            navController.navigate(Routes.category(key))
                        }
                    },
                    onProfileClick = {
                        navController.navigate(Routes.PROFILE)
                    }
                )
            }

            // --- ARAMA SAYFASI ---
            composable(Routes.SEARCH) {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Routes.detail(movieId))
                    }
                )
            }

            // --- DETAY SAYFASI ---
            composable(
                route = Routes.DETAIL_WITH_ARG,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { entry ->
                val movieId = entry.arguments?.getInt("movieId") ?: 0
                DetailScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() }
                )
            }

            // --- KATEGORY SAYFASI ---
            composable(
                route = Routes.CATEGORY_WITH_ARG,
                arguments = listOf(navArgument("categoryType") { type = NavType.StringType })
            ) {
                CategoryScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { movieId ->
                        navController.navigate(Routes.detail(movieId))
                    }
                )
            }

            // --- ✅ GÜNCELLENEN KISIM: PROFİL SAYFASI ---
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    isDarkTheme = isDarkTheme,       // ✅ Veriyi ilet
                    onThemeChanged = onThemeChanged  // ✅ Fonksiyonu ilet
                )
            }
        }
    }
}
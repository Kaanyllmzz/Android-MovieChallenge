package com.kaan.moviechallenge.presentation.navigation

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val DETAIL = "detail"

    // ✅ YENİ: Profil Sayfası Rotası
    const val PROFILE = "profile"

    // Detay Sayfası Rotası
    const val DETAIL_WITH_ARG = "detail/{movieId}"
    fun detail(movieId: Int) = "detail/$movieId"

    // Kategori (Show All) Sayfası Rotası
    const val CATEGORY_WITH_ARG = "category/{categoryType}"
    fun category(categoryType: String) = "category/$categoryType"
}
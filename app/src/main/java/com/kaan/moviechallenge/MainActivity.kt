package com.kaan.moviechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kaan.moviechallenge.presentation.navigation.AppNavHost
import com.kaan.moviechallenge.ui.theme.MoviechallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 1. Başlangıçta telefonun kendi ayarını (Sistem Teması) alıyoruz
            val systemTheme = isSystemInDarkTheme()

            // 2. Bunu değiştirilebilir bir "Durum" (State) haline getiriyoruz
            var isDarkTheme by remember { mutableStateOf(systemTheme) }

            // 3. Temayı bu duruma göre oluşturuyoruz
            MoviechallengeTheme(darkTheme = isDarkTheme) {

                // 4. Navigasyona bu durumu ve değiştirecek kumandayı veriyoruz
                AppNavHost(
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { newTheme -> isDarkTheme = newTheme }
                )
            }
        }
    }
}

package com.example.uthtodo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.uthtodo.data.AppSettings
import com.example.uthtodo.data.SettingsRepository
import com.example.uthtodo.data.TodoRepository
import com.example.uthtodo.navigation.TodoNavHost
import com.example.uthtodo.ui.components.TodoColors
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        "UTH_FCM",
                        "Không lấy được Token",
                        task.exception
                    )
                    return@addOnCompleteListener
                }

                Log.d(
                    "UTH_FCM",
                    "FCM Token: ${task.result}"
                )
            }

        enableEdgeToEdge()

        setContent {
            val settingsRepository = remember {
                SettingsRepository(applicationContext)
            }

            // null khi đang đọc dữ liệu lần đầu
            val loadedSettings by settingsRepository.settings
                .collectAsState(initial = null)

            val settings = loadedSettings

            if (settings != null) {
                val systemDark = isSystemInDarkTheme()

                val darkMode = when (settings.themeMode) {
                    "dark" -> true
                    "auto" -> systemDark
                    else -> false
                }

                val primary = when (settings.accentColor) {
                    1 -> Color(0xFF128866)  // Xanh lá
                    2 -> Color(0xFFD34B16)  // Cam
                    3 -> Color(0xFF8039F6)  // Tím
                    else -> Color(0xFF315BEF) // Xanh dương
                }

                val colorScheme = if (darkMode) {
                    darkColorScheme(
                        primary = primary,
                        onPrimary = Color.White,
                        background = Color(0xFF101012),
                        onBackground = Color(0xFFF5F5F8),
                        surface = Color(0xFF1F1F22),
                        onSurface = Color(0xFFF5F5F8),
                        surfaceVariant = Color(0xFF29292D),
                        onSurfaceVariant = Color(0xFFCACAD0)
                    )
                } else {
                    // Giữ nền sáng như thiết kế gốc
                    lightColorScheme(
                        primary = primary,
                        onPrimary = Color.White,
                        background = Color(0xFFF5F5F8),
                        onBackground = Color(0xFF222227),
                        surface = Color.White,
                        onSurface = Color(0xFF222227),
                        surfaceVariant = Color(0xFFF0F0F4),
                        onSurfaceVariant = Color(0xFF777780)
                    )
                }

                // Đồng bộ màu dùng chung
                LaunchedEffect(darkMode, primary) {
                    TodoColors.updateTheme(
                        dark = darkMode,
                        accent = primary
                    )
                }

                val deviceDensity = LocalDensity.current

                val adjustedDensity = remember(
                    deviceDensity.density,
                    deviceDensity.fontScale,
                    settings.fontScale
                ) {
                    Density(
                        density = deviceDensity.density,
                        fontScale = deviceDensity.fontScale *
                                settings.fontScale
                    )
                }

                CompositionLocalProvider(
                    LocalDensity provides adjustedDensity
                ) {
                    MaterialTheme(
                        colorScheme = colorScheme
                    ) {
                        val repository = remember {
                            TodoRepository()
                        }

                        TodoNavHost(
                            repository = repository
                        )
                    }
                }
            }
        }
    }
}

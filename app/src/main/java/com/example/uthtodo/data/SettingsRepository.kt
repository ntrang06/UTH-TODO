
package com.example.uthtodo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "uth_todo_settings"
)

data class AppSettings(
    val themeMode: String = "light",
    val accentColor: Int = 0,
    val fontScale: Float = 1f,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = false
)

class SettingsRepository(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private val THEME_MODE =
            stringPreferencesKey("theme_mode")

        private val ACCENT_COLOR =
            intPreferencesKey("accent_color")

        private val FONT_SCALE =
            floatPreferencesKey("font_scale")

        private val NOTIFICATIONS_ENABLED =
            booleanPreferencesKey("notifications_enabled")

        private val SOUND_ENABLED =
            booleanPreferencesKey("sound_enabled")

        private val DAILY_SUMMARY =
            booleanPreferencesKey("daily_summary")
    }

    val settings: Flow<AppSettings> =
        appContext.settingsDataStore.data.map { preferences ->
            AppSettings(
                themeMode = preferences[THEME_MODE] ?: "light",
                accentColor = preferences[ACCENT_COLOR] ?: 0,
                fontScale = preferences[FONT_SCALE] ?: 1f,
                notificationsEnabled =
                    preferences[NOTIFICATIONS_ENABLED] ?: true,
                soundEnabled =
                    preferences[SOUND_ENABLED] ?: true,
                dailySummaryEnabled =
                    preferences[DAILY_SUMMARY] ?: false
            )
        }

    // Giữ tương thích với ProfileScreen
    val notificationsEnabled: Flow<Boolean> =
        settings.map { it.notificationsEnabled }

    suspend fun setThemeMode(mode: String) {
        appContext.settingsDataStore.edit {
            it[THEME_MODE] = mode
        }
    }

    suspend fun setAccentColor(index: Int) {
        appContext.settingsDataStore.edit {
            it[ACCENT_COLOR] = index.coerceIn(0, 3)
        }
    }

    suspend fun setFontScale(scale: Float) {
        appContext.settingsDataStore.edit {
            it[FONT_SCALE] = scale.coerceIn(0.85f, 1.25f)
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        appContext.settingsDataStore.edit {
            it[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        appContext.settingsDataStore.edit {
            it[SOUND_ENABLED] = enabled
        }
    }

    suspend fun setDailySummaryEnabled(enabled: Boolean) {
        appContext.settingsDataStore.edit {
            it[DAILY_SUMMARY] = enabled
        }
    }
}

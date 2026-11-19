
package com.example.uthtodo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Tạo DataStore dùng chung trong ứng dụng
private val Context.dataStore by preferencesDataStore(
    name = "uth_todo_settings"
)

class SettingsRepository(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_ENABLED =
            booleanPreferencesKey("notifications_enabled")
    }

    // Đọc trạng thái thông báo đã lưu
    val notificationsEnabled: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    // Lưu trạng thái bật/tắt thông báo
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}

package com.example.uthtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.remember
import com.example.uthtodo.data.TodoRepository
import com.example.uthtodo.navigation.TodoNavHost
import com.example.uthtodo.ui.components.TodoColors

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("UTH_FCM", "Không lấy được Token", task.exception)
                    return@addOnCompleteListener
                }

                Log.d("UTH_FCM", "FCM Token: ${task.result}")
            }


        enableEdgeToEdge()

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = TodoColors.Blue,
                    background = TodoColors.White,
                    surface = TodoColors.White,
                    onPrimary = TodoColors.White,
                    onBackground = TodoColors.Ink,
                    onSurface = TodoColors.Ink
                )
            ) {
                // Tạo đối tượng cung cấp dữ liệu.
                val repository = remember {
                    TodoRepository()
                }

                // Khởi chạy hệ thống điều hướng.
                TodoNavHost(
                    repository = repository
                )
            }
        }
    }
}
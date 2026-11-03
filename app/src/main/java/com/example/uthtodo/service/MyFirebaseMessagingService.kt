
package com.example.uthtodo.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("UTH_FCM", "FCM Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("UTH_FCM", "Nhận thông báo Firebase")
        Log.d("UTH_FCM", "Tiêu đề: ${message.notification?.title}")
        Log.d("UTH_FCM", "Nội dung: ${message.notification?.body}")
    }
}

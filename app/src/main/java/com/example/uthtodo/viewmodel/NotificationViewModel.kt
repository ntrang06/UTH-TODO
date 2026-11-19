
package com.example.uthtodo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class TodoNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    UPCOMING,
    OVERDUE,
    COMPLETED,
    REMINDER
}

class NotificationViewModel : ViewModel() {

    // Danh sách thông báo mẫu
    var notifications by mutableStateOf(
        listOf(
            TodoNotification(
                id = 1,
                title = "Sắp đến hạn",
                message = "Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00.",
                time = "13:45",
                type = NotificationType.UPCOMING
            ),
            TodoNotification(
                id = 2,
                title = "Đã quá hạn",
                message = "Bài đọc tham khảo môn Mạng máy tính chưa xong.",
                time = "10:10",
                type = NotificationType.OVERDUE
            ),
            TodoNotification(
                id = 3,
                title = "Làm tốt lắm!",
                message = "Bạn đã xong 2 việc trong sáng nay.",
                time = "11:30",
                type = NotificationType.COMPLETED,
                isRead = true
            ),
            TodoNotification(
                id = 4,
                title = "Nhắc việc",
                message = "Họp nhóm đồ án trong tuần này.",
                time = "Hôm qua",
                type = NotificationType.REMINDER
            )
        )
    )
        private set

    // Đánh dấu tất cả thông báo đã đọc
    fun markAllAsRead() {
        notifications = notifications.map {
            it.copy(isRead = true)
        }
    }

    // Xóa một thông báo theo ID
    fun deleteNotification(id: Int) {
        notifications = notifications.filter {
            it.id != id
        }
    }

    // Xóa tất cả thông báo
    fun clearAllNotifications() {
        notifications = emptyList()
    }
}

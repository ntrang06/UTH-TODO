package com.example.uthtodo.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class TodoCategory(val title: String) {
    STUDY("Học tập"),
    LIFE("Cuộc sống")
}

enum class TaskPriority(val title: String) {
    LOW("Thấp"),
    MEDIUM("Vừa"),
    HIGH("Cao")
}

data class TodoTask(
    val id: String,
    val title: String,
    val category: TodoCategory,
    val time: String,
    val isCompleted: Boolean = false,
    val note: String = "",
    val dueAtMillis: Long = System.currentTimeMillis(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val completedAtMillis: Long? = null
)

data class TaskStatistics(
    val completed: Int,
    val total: Int
) {
    val pending: Int
        get() = total - completed

    val fraction: Float
        get() = if (total == 0) 0f else completed.toFloat() / total

    val percent: Int
        get() = (fraction * 100).roundToInt()

    companion object {
        fun from(tasks: List<TodoTask>): TaskStatistics {
            return TaskStatistics(
                completed = tasks.count { it.isCompleted },
                total = tasks.size
            )
        }
    }
}

data class WeekDayProgress(
    val label: String,
    val completed: Int,
    val isToday: Boolean
)

data class DashboardContent(
    val guestName: String = "Bạn",
    val guestInitials: String = "UT",
    val todayProgress: String = "Tiến độ hôm nay",
    val encouragement: String = "Bắt đầu từ việc nhỏ nhất nhé.",
    val todayTasks: String = "Việc hôm nay",
    val allTasks: String = "Tất cả việc hôm nay",
    val viewAll: String = "Xem tất cả",
    val collapse: String = "Thu gọn",
    val homeTab: String = "Trang chủ",
    val listTab: String = "Danh sách",
    val notificationsTab: String = "Thông báo",
    val settingsTab: String = "Cài đặt",
    val addTask: String = "Thêm công việc",
    val openProfile: String = "Mở hồ sơ",
    val markDone: String = "Đánh dấu hoàn thành",
    val markPending: String = "Đánh dấu chưa hoàn thành"
) {
    fun greeting(user: AppUser?) =
        "Chào ${user?.displayName ?: guestName}!"

    fun progress(stats: TaskStatistics) =
        "${stats.completed}/${stats.total} việc"

    fun remaining(count: Int) = "$count việc còn lại"

    fun todayDate(): String {
        val locale = Locale.forLanguageTag("vi-VN")

        return SimpleDateFormat(
            "EEEE, d 'tháng' M",
            locale
        ).format(Date()).replaceFirstChar {
            it.titlecase(locale)
        }
    }
}

data class ProfileContent(
    val title: String = "Hồ sơ",
    val back: String = "Trang chủ",
    val guestName: String = "Khách UTH",
    val guestDescription: String = "Đang dùng thử không cần tài khoản",
    val googleLinked: String = "Đã liên kết tài khoản Google",
    val completed: String = "Việc đã xong",
    val pending: String = "Đang chờ",
    val completion: String = "Hoàn thành",
    val weekTitle: String = "Tuần này",
    val weekDescription: String = "Việc xong mỗi ngày",
    val accountTitle: String = "Tài khoản",
    val provider: String = "Đăng nhập bằng",
    val google: String = "Google",
    val guest: String = "Khách",
    val email: String = "Email",
    val noEmail: String = "Chưa cung cấp",
    val logout: String = "Đăng xuất",
    val login: String = "Đăng nhập với Google",
    val loggingOut: String = "Đang đăng xuất…",
    val logoutSuccess: String = "Đã đăng xuất",
    val logoutError: String = "Chưa đăng xuất được. Vui lòng thử lại.",
    val credentialWarning: String =
        "Đã đăng xuất. Bộ chọn Google có thể vẫn gợi ý tài khoản cũ."
) {
    fun weekProgress(tasks: List<TodoTask>): List<WeekDayProgress> {
        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

        val now = Calendar.getInstance()
        val today = (now.get(Calendar.DAY_OF_WEEK) + 5) % 7

        val monday = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, -today)
        }

        return labels.mapIndexed { index, label ->
            val date = (monday.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, index)
            }.timeInMillis

            val count = tasks.count {
                it.isCompleted && TaskDates.sameDay(
                    it.completedAtMillis ?: it.dueAtMillis,
                    date
                )
            }

            WeekDayProgress(
                label = label,
                completed = count,
                isToday = index == today
            )
        }
    }
}
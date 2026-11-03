package com.example.uthtodo.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TaskDates {
    private fun format(value: Long, pattern: String): String {
        return SimpleDateFormat(
            pattern,
            Locale.forLanguageTag("vi-VN")
        ).format(Date(value))
    }

    fun date(value: Long) = format(value, "dd/MM/yyyy")

    fun time(value: Long) = format(value, "HH:mm")

    fun sameDay(first: Long, second: Long): Boolean {
        return format(first, "yyyyMMdd") == format(second, "yyyyMMdd")
    }

    fun isToday(task: TodoTask): Boolean {
        return sameDay(
            task.dueAtMillis,
            System.currentTimeMillis()
        )
    }
}

data class TaskFeed(
    val tasks: List<TodoTask> = emptyList(),
    val isLoading: Boolean = true,
    val hasPendingWrites: Boolean = false,
    val fromCache: Boolean = false,
    val error: String? = null
)

data class TaskContent(
    val listTitle: String = "Danh sách",
    val search: String = "Tìm công việc",
    val all: String = "Tất cả",
    val pending: String = "CẦN LÀM",
    val completed: String = "ĐÃ XONG",
    val empty: String =
        "Chưa có công việc. Nhấn + để thêm việc đầu tiên.",
    val emptyToday: String =
        "Hôm nay chưa có công việc. Nhấn + để thêm.",
    val noResults: String = "Không có công việc phù hợp.",
    val loading: String = "Đang tải công việc…",
    val syncing: String = "Đang đồng bộ thay đổi…",
    val cached: String = "Đang hiển thị dữ liệu trên thiết bị.",
    val retry: String = "Thử lại",
    val errorTitle: String = "Chưa lưu được thay đổi",
    val close: String = "Đóng",
    val newTask: String = "Việc mới",
    val cancel: String = "Hủy",
    val save: String = "Lưu",
    val saveTask: String = "Lưu công việc",
    val titleLabel: String = "Tên công việc",
    val titleHint: String = "Ví dụ: Ôn chương 4 – Giải thuật",
    val noteLabel: String = "Ghi chú",
    val noteHint: String = "Thêm ghi chú (không bắt buộc)",
    val category: String = "Nhóm",
    val date: String = "Ngày",
    val time: String = "Giờ",
    val priority: String = "Mức ưu tiên",
    val invalidTitle: String =
        "Nhập tên công việc từ 1 đến 150 ký tự.",
    val invalidDate: String =
        "Vui lòng chọn ngày và giờ sau mốc 01/01/1970.",
    val noteTooLong: String = "Ghi chú tối đa 2000 ký tự."
) {
    fun pendingCount(tasks: List<TodoTask>): String {
        return "${tasks.count { !it.isCompleted }} việc chưa xong"
    }
}
package com.example.uthtodo.data

import com.example.uthtodo.model.TodoCategory
import com.example.uthtodo.model.TodoTask

// Dữ liệu mẫu trong bộ nhớ, chưa ghi lên Firestore.
class DashboardRepository {

    private val sampleTasks = listOf(
        TodoTask(
            id = "1",
            title = "Nộp bài tập Lập trình di động",
            category = TodoCategory.STUDY,
            time = "09:00",
            isCompleted = true
        ),
        TodoTask(
            id = "2",
            title = "Ôn chương 3 – Cấu trúc dữ liệu",
            category = TodoCategory.STUDY,
            time = "14:00"
        ),
        TodoTask(
            id = "3",
            title = "Đi chợ mua đồ cho tuần",
            category = TodoCategory.LIFE,
            time = "17:30"
        ),
        TodoTask(
            id = "4",
            title = "Tập thể dục 30 phút",
            category = TodoCategory.LIFE,
            time = "06:30",
            isCompleted = true
        ),
        TodoTask(
            id = "5",
            title = "Đọc 20 trang sách",
            category = TodoCategory.LIFE,
            time = "07:30",
            isCompleted = true
        ),
        TodoTask(
            id = "6",
            title = "Họp nhóm đồ án",
            category = TodoCategory.STUDY,
            time = "20:00"
        ),
        TodoTask(
            id = "7",
            title = "Dọn góc học tập",
            category = TodoCategory.LIFE,
            time = "21:00"
        )
    )

    fun getTasks(): List<TodoTask> {
        return sampleTasks.toList()
    }

    fun toggleTask(
        tasks: List<TodoTask>,
        id: String
    ): List<TodoTask> {
        return tasks.map { task ->
            if (task.id == id) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
    }
}
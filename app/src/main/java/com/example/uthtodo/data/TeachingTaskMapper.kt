
package com.example.uthtodo.data

import com.example.uthtodo.model.TaskPriority
import com.example.uthtodo.model.TodoCategory
import com.example.uthtodo.model.TodoTask
import java.text.SimpleDateFormat
import java.util.Locale

fun TeachingTask.toTodoTask(): TodoTask {

    val formatter = SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        Locale.US
    ).apply {
        isLenient = false
    }

    val dueDate = formatter.parse("$date $time")
        ?: throw IllegalArgumentException(
            "Ngày giờ không hợp lệ: $date $time"
        )

    val taskCategory = when (category.lowercase()) {
        "study" -> TodoCategory.STUDY
        "life" -> TodoCategory.LIFE
        else -> TodoCategory.LIFE
    }

    val taskPriority = when (priority.lowercase()) {
        "low" -> TaskPriority.LOW
        "high" -> TaskPriority.HIGH
        else -> TaskPriority.MEDIUM
    }

    return TodoTask(
        id = id.toString(),
        title = title,
        category = taskCategory,
        time = time,
        isCompleted = done,
        note = note,
        dueAtMillis = dueDate.time,
        priority = taskPriority,
        completedAtMillis = null
    )
}

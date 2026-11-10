
package com.example.uthtodo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uthtodo.data.TeachingTask
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditTaskScreen(
    task: TeachingTask,
    isSaving: Boolean,
    onCancel: () -> Unit,
    onSave: (TeachingTask) -> Unit
) {
    var title by remember(task.id) { mutableStateOf(task.title) }
    var note by remember(task.id) { mutableStateOf(task.note) }
    var date by remember(task.id) { mutableStateOf(task.date) }
    var time by remember(task.id) { mutableStateOf(task.time) }
    var category by remember(task.id) { mutableStateOf(task.category) }
    var priority by remember(task.id) { mutableStateOf(task.priority) }
    var remind by remember(task.id) { mutableStateOf(task.remind) }
    var validationError by remember(task.id) {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onCancel()
        },
        title = {
            Text("Sửa công việc")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên công việc") },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú") },
                    minLines = 2,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Danh mục")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = category == "study",
                        onClick = { category = "study" },
                        enabled = !isSaving,
                        label = { Text("Học tập") }
                    )
                    FilterChip(
                        selected = category == "life",
                        onClick = { category = "life" },
                        enabled = !isSaving,
                        label = { Text("Cuộc sống") }
                    )
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Ngày (yyyy-MM-dd)") },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Giờ (HH:mm)") },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Mức ưu tiên")

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "low" to "Thấp",
                        "medium" to "Vừa",
                        "high" to "Cao"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = priority == value,
                            onClick = { priority = value },
                            enabled = !isSaving,
                            label = { Text(label) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nhắc nhở",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Switch(
                        checked = remind,
                        onCheckedChange = { remind = it },
                        enabled = !isSaving
                    )
                }

                if (validationError != null) {
                    Text(
                        text = validationError.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (isSaving) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    val formatter = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.US
                    ).apply {
                        isLenient = false
                    }

                    val validDateTime = try {
                        formatter.parse("$date $time") != null
                    } catch (_: Exception) {
                        false
                    }

                    validationError = when {
                        title.isBlank() || title.length > 150 ->
                            "Tên công việc phải có 1–150 ký tự."
                        note.length > 2000 ->
                            "Ghi chú tối đa 2000 ký tự."
                        !validDateTime ->
                            "Ngày giờ không hợp lệ."
                        else -> null
                    }

                    if (validationError == null) {
                        onSave(
                            task.copy(
                                title = title.trim(),
                                note = note,
                                category = category,
                                date = date,
                                time = time,
                                priority = priority,
                                remind = remind
                            )
                        )
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isSaving
            ) {
                Text("Hủy")
            }
        }
    )
}

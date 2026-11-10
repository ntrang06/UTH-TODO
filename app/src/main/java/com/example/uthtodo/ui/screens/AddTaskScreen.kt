
package com.example.uthtodo.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

@Composable
fun AddTaskScreen(
    content: TaskContent,
    onCancel: () -> Unit,
    onSave: (TodoTask) -> Unit,
    onSaveApi: (suspend (TodoTask, Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by rememberSaveable {
        mutableStateOf("")
    }

    var note by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf(TodoCategory.STUDY.name)
    }

    var priority by rememberSaveable {
        mutableStateOf(TaskPriority.MEDIUM.name)
    }

    var dueAt by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var remind by rememberSaveable {
        mutableStateOf(true)
    }

    var error by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    val save: () -> Unit = save@{
        if (isSaving || submitted) {
            return@save
        }

        error = when {
            title.trim().isEmpty() ||
                    title.trim().length > 150 ->
                content.invalidTitle

            note.length > 2000 ->
                content.noteTooLong

            dueAt <= 0L ->
                content.invalidDate

            else -> null
        }

        if (error != null) {
            return@save
        }

        val newTask = TodoTask(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            category = TodoCategory.valueOf(category),
            time = TaskDates.time(dueAt),
            isCompleted = false,
            note = note.trim(),
            dueAtMillis = dueAt,
            priority = TaskPriority.valueOf(priority)
        )

        if (onSaveApi != null) {
            // Bài tuần 04: POST API
            scope.launch {
                isSaving = true

                try {
                    onSaveApi(newTask, remind)
                    submitted = true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    error = e.localizedMessage
                        ?: "Không thêm được công việc qua API."
                } finally {
                    isSaving = false
                }
            }
        } else {
            // Giữ luồng Firebase cũ
            submitted = true
            onSave(newTask)
        }
    }

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = save,
                    enabled = !isSaving && !submitted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(Modifier.width(10.dp))
                    }

                    Text(
                        if (isSaving) {
                            "Đang gửi POST..."
                        } else {
                            content.saveTask
                        }
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onCancel,
                    enabled = !isSaving
                ) {
                    Text(content.cancel)
                }

                Text(
                    text = content.newTask,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = save,
                    enabled = !isSaving && !submitted
                ) {
                    Text(content.save)
                }
            }

            // Tên công việc
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    error = null
                },
                label = {
                    Text(content.titleLabel)
                },
                placeholder = {
                    Text(content.titleHint)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Ghi chú
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                    error = null
                },
                label = {
                    Text(content.noteLabel)
                },
                placeholder = {
                    Text(content.noteHint)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Danh mục
            Text(
                text = content.category,
                color = TodoColors.Gray,
                fontSize = 13.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TodoCategory.entries.forEach { item ->
                    FilterChip(
                        selected = category == item.name,
                        onClick = {
                            category = item.name
                        },
                        enabled = !isSaving,
                        label = {
                            Text(item.title)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Ngày và giờ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp)
            ) {
                TextButton(
                    onClick = {
                        val date = Calendar.getInstance().apply {
                            timeInMillis = dueAt
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dueAt = (
                                        date.clone() as Calendar
                                        ).apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, day)
                                    }.timeInMillis
                            },
                            date.get(Calendar.YEAR),
                            date.get(Calendar.MONTH),
                            date.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content.date,
                        modifier = Modifier.weight(1f),
                        color = TodoColors.Ink
                    )

                    Text(TaskDates.date(dueAt))
                }

                HorizontalDivider(
                    color = DashboardBackground
                )

                TextButton(
                    onClick = {
                        val date = Calendar.getInstance().apply {
                            timeInMillis = dueAt
                        }

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                dueAt = (
                                        date.clone() as Calendar
                                        ).apply {
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.timeInMillis
                            },
                            date.get(Calendar.HOUR_OF_DAY),
                            date.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content.time,
                        modifier = Modifier.weight(1f),
                        color = TodoColors.Ink
                    )

                    Text(TaskDates.time(dueAt))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Mức ưu tiên
            Text(
                text = content.priority,
                color = TodoColors.Gray,
                fontSize = 13.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { item ->
                    FilterChip(
                        selected = priority == item.name,
                        onClick = {
                            priority = item.name
                        },
                        enabled = !isSaving,
                        label = {
                            Text(item.title)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Nhắc nhở
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nhắc nhở",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = TodoColors.Ink
                )

                Switch(
                    checked = remind,
                    onCheckedChange = {
                        remind = it
                    },
                    enabled = !isSaving
                )
            }

            Spacer(Modifier.height(16.dp))

            // Hiển thị lỗi nếu POST thất bại
            error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            if (isSaving) {
                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Đang gửi dữ liệu lên API...",
                    color = TodoColors.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

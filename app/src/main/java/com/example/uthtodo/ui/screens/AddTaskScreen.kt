
package com.example.uthtodo.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

    // Bảng màu dùng chung
    val dark = TodoColors.isDark
    val background = TodoColors.Background
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue
    val line = TodoColors.Line

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

    // Màu cho các lựa chọn
    val selectedBackground = if (dark) {
        primary.copy(alpha = 0.22f)
    } else {
        primary.copy(alpha = 0.12f)
    }

    val selectedTextColor = if (dark) {
        Color.White
    } else {
        primary
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ink,
        unfocusedTextColor = ink,
        disabledTextColor = gray,

        focusedContainerColor = surface,
        unfocusedContainerColor = surface,
        disabledContainerColor = surface,

        focusedBorderColor = primary,
        unfocusedBorderColor = line,
        disabledBorderColor = line,

        focusedLabelColor = primary,
        unfocusedLabelColor = gray,

        focusedPlaceholderColor = gray,
        unfocusedPlaceholderColor = gray,

        cursorColor = primary
    )

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = surface,
        labelColor = ink,
        selectedContainerColor = selectedBackground,
        selectedLabelColor = selectedTextColor,
        disabledContainerColor = surface,
        disabledLabelColor = gray
    )

    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = line,
        selectedBorderColor = primary
    )

    // Lưu công việc
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
            // Giữ chức năng POST API
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
        containerColor = background,

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = Color.White,
                        disabledContainerColor = primary.copy(
                            alpha = 0.40f
                        ),
                        disabledContentColor = Color.White
                    )
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
                        text = if (isSaving) {
                            "Đang gửi POST..."
                        } else {
                            content.saveTask
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
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

            // THANH TIÊU ĐỀ
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
                    Text(
                        text = content.cancel,
                        color = primary
                    )
                }

                Text(
                    text = content.newTask,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = ink
                )

                TextButton(
                    onClick = save,
                    enabled = !isSaving && !submitted
                ) {
                    Text(
                        text = content.save,
                        color = if (isSaving || submitted) {
                            gray
                        } else {
                            primary
                        }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // TÊN CÔNG VIỆC
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
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors
            )

            Spacer(Modifier.height(12.dp))

            // GHI CHÚ
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
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors
            )

            Spacer(Modifier.height(20.dp))

            // NHÓM CÔNG VIỆC
            Text(
                text = content.category,
                color = gray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TodoCategory.entries.forEach { item ->
                    val isSelected = category == item.name

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            category = item.name
                        },
                        enabled = !isSaving,
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 13.sp,
                                color = if (isSelected) {
                                    selectedTextColor
                                } else {
                                    ink
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = chipColors,
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = !isSaving,
                            selected = isSelected,
                            borderColor = line,
                            selectedBorderColor = primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // NGÀY VÀ GIỜ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp)
            ) {

                // CHỌN NGÀY
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
                        color = ink
                    )

                    Text(
                        text = TaskDates.date(dueAt),
                        color = primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider(
                    color = line,
                    thickness = 1.dp
                )

                // CHỌN GIỜ
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
                        color = ink
                    )

                    Text(
                        text = TaskDates.time(dueAt),
                        color = primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // MỨC ƯU TIÊN
            Text(
                text = content.priority,
                color = gray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { item ->
                    val isSelected = priority == item.name

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            priority = item.name
                        },
                        enabled = !isSaving,
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 13.sp,
                                color = if (isSelected) {
                                    selectedTextColor
                                } else {
                                    ink
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = chipColors,
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = !isSaving,
                            selected = isSelected,
                            borderColor = line,
                            selectedBorderColor = primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // NHẮC NHỞ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
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
                    color = ink
                )

                Switch(
                    checked = remind,
                    onCheckedChange = {
                        remind = it
                    },
                    enabled = !isSaving,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = primary,
                        checkedThumbColor = Color.White,
                        uncheckedTrackColor = if (dark) {
                            Color(0xFF48484E)
                        } else {
                            Color(0xFFD3D3D9)
                        },
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = line
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // LỖI KHI GỬI API
            error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            // TRẠNG THÁI ĐANG LƯU
            if (isSaving) {
                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = primary,
                    trackColor = line
                )

                Text(
                    text = "Đang gửi dữ liệu lên API...",
                    color = gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

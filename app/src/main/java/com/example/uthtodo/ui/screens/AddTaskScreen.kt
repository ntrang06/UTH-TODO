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
import java.util.Calendar
import java.util.UUID

@Composable
fun AddTaskScreen(
    content: TaskContent,
    onCancel: () -> Unit,
    onSave: (TodoTask) -> Unit
) {
    val context = LocalContext.current

    var title by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    var category by rememberSaveable {
        mutableStateOf(TodoCategory.STUDY.name)
    }

    var priority by rememberSaveable {
        mutableStateOf(TaskPriority.MEDIUM.name)
    }

    var dueAt by rememberSaveable {
        mutableStateOf(System.currentTimeMillis())
    }

    var error by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    val save: () -> Unit = {
        error = when {
            title.trim().isEmpty() || title.trim().length > 150 ->
                content.invalidTitle

            note.length > 2000 ->
                content.noteTooLong

            dueAt <= 0L ->
                content.invalidDate

            else -> null
        }

        if (error == null && !submitted) {
            submitted = true

            onSave(
                TodoTask(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    category = TodoCategory.valueOf(category),
                    time = TaskDates.time(dueAt),
                    note = note.trim(),
                    dueAtMillis = dueAt,
                    priority = TaskPriority.valueOf(priority)
                )
            )
        }
    }

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = save,
                    enabled = !submitted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(content.saveTask)
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text(content.cancel)
                }

                Text(
                    content.newTask,
                    Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = save,
                    enabled = !submitted
                ) {
                    Text(content.save)
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(content.titleLabel) },
                placeholder = { Text(content.titleHint) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(content.noteLabel) },
                placeholder = { Text(content.noteHint) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                content.category,
                color = TodoColors.Gray,
                fontSize = 13.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TodoCategory.entries.forEach { item ->
                    FilterChip(
                        selected = category == item.name,
                        onClick = { category = item.name },
                        label = { Text(item.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                Modifier
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
                                dueAt = (date.clone() as Calendar).apply {
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        content.date,
                        Modifier.weight(1f),
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
                                dueAt = (date.clone() as Calendar).apply {
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        content.time,
                        Modifier.weight(1f),
                        color = TodoColors.Ink
                    )

                    Text(TaskDates.time(dueAt))
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                content.priority,
                color = TodoColors.Gray,
                fontSize = 13.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { item ->
                    FilterChip(
                        selected = priority == item.name,
                        onClick = { priority = item.name },
                        label = { Text(item.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
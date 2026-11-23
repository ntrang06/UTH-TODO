
package com.example.uthtodo.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*
import kotlinx.coroutines.launch

// Tạo nội dung TXT từ danh sách công việc
private fun buildTaskExportText(tasks: List<TodoTask>): String {
    return buildString {
        appendLine("UTH TO DO - DANH SACH CONG VIEC")
        appendLine("==============================")
        appendLine("Tong so cong viec: ${tasks.size}")
        appendLine(
            "Chua hoan thanh: ${tasks.count { !it.isCompleted }}"
        )
        appendLine(
            "Da hoan thanh: ${tasks.count { it.isCompleted }}"
        )
        appendLine()

        if (tasks.isEmpty()) {
            appendLine("Khong co cong viec de xuat.")
        } else {
            tasks.forEachIndexed { index, task ->
                appendLine("${index + 1}. ${task.title}")
                appendLine("Nhom: ${task.category.title}")
                appendLine(
                    "Ngay: ${TaskDates.date(task.dueAtMillis)}"
                )
                appendLine("Gio: ${task.time}")
                appendLine("Uu tien: ${task.priority.title}")
                appendLine(
                    "Trang thai: " +
                            if (task.isCompleted) {
                                "Da hoan thanh"
                            } else {
                                "Chua hoan thanh"
                            }
                )

                if (task.note.isNotBlank()) {
                    appendLine("Ghi chu: ${task.note}")
                }

                appendLine("------------------------------")
            }
        }
    }
}

@Composable
fun TaskListScreen(
    feed: TaskFeed,
    content: TaskContent,
    dashboard: DashboardContent,
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    onToggle: (String) -> Unit,
    onRetry: () -> Unit,
    onOpenTask: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var query by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf("")
    }

    val visible = feed.tasks.filter {
        (category.isEmpty() ||
                it.category.name == category) &&
                (
                        it.title.contains(
                            query.trim(),
                            ignoreCase = true
                        ) ||
                                it.note.contains(
                                    query.trim(),
                                    ignoreCase = true
                                )
                        )
    }

    val background = TodoColors.Background
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue

    // Giữ nội dung chuẩn bị xuất trong lúc
    // Android mở cửa sổ chọn nơi lưu.
    var pendingExportText by remember {
        mutableStateOf("")
    }

    // Storage Access Framework - CreateDocument
    val exportTxtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "text/plain"
        )
    ) { uri ->
        if (uri != null) {
            try {
                val output = context.contentResolver
                    .openOutputStream(uri)
                    ?: throw IllegalStateException(
                        "Không mở được tệp để ghi."
                    )

                output.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(pendingExportText)
                }

                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Đã xuất tệp TXT thành công!"
                    )
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Xuất tệp thất bại: " +
                                (e.localizedMessage ?: "Lỗi không xác định")
                    )
                }
            }
        }

        pendingExportText = ""
    }

    Scaffold(
        containerColor = background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        bottomBar = {
            DashboardBottomBar(
                content = dashboard,
                selected = DashboardIcon.LIST,
                onHome = onHome,
                onAdd = onAdd,
                onNotifications = onNotifications,
                onSettings = onSettings
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = content.listTitle,
                    modifier = Modifier.weight(1f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink
                )

                Text(
                    text = content.pendingCount(feed.tasks),
                    fontSize = 11.sp,
                    color = gray
                )
            }

            Spacer(Modifier.height(12.dp))

            // Tìm kiếm
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(content.search)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ink,
                    unfocusedTextColor = ink,
                    focusedContainerColor = TodoColors.Surface,
                    unfocusedContainerColor = TodoColors.Surface,
                    focusedBorderColor = primary,
                    unfocusedBorderColor = TodoColors.Line,
                    focusedPlaceholderColor = gray,
                    unfocusedPlaceholderColor = gray
                )
            )

            Spacer(Modifier.height(8.dp))

            // Bộ lọc công việc
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = category.isEmpty(),
                    onClick = { category = "" },
                    label = {
                        Text(content.all)
                    }
                )

                TodoCategory.entries.forEach { item ->
                    FilterChip(
                        selected = category == item.name,
                        onClick = {
                            category = item.name
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // CHƯƠNG 12: XUẤT TXT BẰNG SAF
            OutlinedButton(
                onClick = {
                    pendingExportText = buildTaskExportText(visible)

                    exportTxtLauncher.launch("UTH_TODO.txt")
                },
                enabled = !feed.isLoading && feed.error == null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primary
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = primary
                )
            ) {
                Text(
                    text = "↓  Xuất danh sách ra tệp TXT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "Xuất các công việc đang hiển thị bằng SAF",
                fontSize = 11.sp,
                color = gray,
                modifier = Modifier.padding(
                    start = 4.dp,
                    bottom = 4.dp
                )
            )

            // Trạng thái API
            TaskSyncBanner(
                feed = feed,
                content = content,
                onRetry = onRetry
            )

            // Danh sách công việc
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                if (
                    visible.isEmpty() &&
                    !feed.isLoading &&
                    feed.error == null
                ) {
                    item {
                        Text(
                            text = if (feed.tasks.isEmpty()) {
                                content.empty
                            } else {
                                content.noResults
                            },
                            modifier = Modifier.padding(
                                vertical = 24.dp
                            ),
                            color = gray
                        )
                    }
                }

                val groups = listOf(
                    false to content.pending,
                    true to content.completed
                )

                groups.forEach { (done, title) ->
                    val group = visible.filter {
                        it.isCompleted == done
                    }

                    if (group.isNotEmpty()) {
                        item {
                            Text(
                                text = title,
                                modifier = Modifier.padding(top = 12.dp),
                                color = gray,
                                fontSize = 12.sp
                            )
                        }

                        items(
                            items = group,
                            key = { it.id }
                        ) { task ->
                            TaskRow(
                                task = task,
                                content = dashboard,
                                onToggle = onToggle,
                                showDate = true,
                                onOpen = {
                                    onOpenTask(task.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSyncBanner(
    feed: TaskFeed,
    content: TaskContent,
    onRetry: () -> Unit,
    onOpenTask: (String) -> Unit = {}
) {
    val message = when {
        feed.error != null -> feed.error
        feed.isLoading -> content.loading
        feed.hasPendingWrites -> content.syncing
        feed.fromCache -> content.cached
        else -> null
    }

    if (message != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (feed.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = TodoColors.Blue
                )

                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                color = if (feed.error != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    TodoColors.Gray
                }
            )

            if (feed.error != null) {
                TextButton(onClick = onRetry) {
                    Text(content.retry)
                }
            }
        }
    }
}

// Component dùng chung cho Trang chủ và Danh sách
@Composable
fun TaskRow(
    task: TodoTask,
    content: DashboardContent,
    onToggle: (String) -> Unit,
    showDate: Boolean = false,
    onOpen: (() -> Unit)? = null
) {
    val dark = TodoColors.isDark
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                surface,
                RoundedCornerShape(16.dp)
            )
            .padding(
                end = 14.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .toggleable(
                    value = task.isCompleted,
                    role = Role.Checkbox,
                    onValueChange = {
                        onToggle(task.id)
                    }
                )
                .semantics {
                    contentDescription =
                        task.title + ": " +
                                if (task.isCompleted) {
                                    content.markPending
                                } else {
                                    content.markDone
                                }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = if (task.isCompleted) {
                            primary
                        } else {
                            surface
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (task.isCompleted) {
                            primary
                        } else {
                            TodoColors.Dot
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Column(
            modifier = if (onOpen != null) {
                Modifier
                    .weight(1f)
                    .clickable(onClick = onOpen)
            } else {
                Modifier.weight(1f)
            }
        ) {
            Text(
                text = task.title,
                fontSize = 13.sp,
                color = if (task.isCompleted) {
                    if (dark) {
                        Color(0xFF8C8C94)
                    } else {
                        Color(0xFF98989D)
                    }
                } else {
                    ink
                },
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )

            Spacer(Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val study = task.category == TodoCategory.STUDY

                val categoryBackground = if (study) {
                    if (dark) {
                        Color(0xFF293452)
                    } else {
                        TodoColors.Pale
                    }
                } else {
                    if (dark) {
                        Color(0xFF422B1A)
                    } else {
                        Color(0xFFFFEEDC)
                    }
                }

                val categoryColor = if (study) {
                    if (
                        dark &&
                        primary == Color(0xFF315BEF)
                    ) {
                        Color(0xFF93B1FF)
                    } else {
                        primary
                    }
                } else {
                    if (dark) {
                        Color(0xFFFFAF68)
                    } else {
                        Color(0xFF9B602C)
                    }
                }

                Text(
                    text = task.category.title,
                    fontSize = 10.sp,
                    color = categoryColor,
                    modifier = Modifier
                        .background(
                            categoryBackground,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (showDate) {
                        "${TaskDates.date(task.dueAtMillis)}, ${task.time}"
                    } else {
                        task.time
                    },
                    color = gray,
                    fontSize = 10.sp
                )
            }
        }

        if (
            task.priority == TaskPriority.HIGH &&
            !task.isCompleted
        ) {
            Text(
                text = task.priority.title,
                fontSize = 10.sp,
                color = if (dark) {
                    Color(0xFFFFB1A8)
                } else {
                    Color(0xFFCF3024)
                },
                modifier = Modifier
                    .background(
                        color = if (dark) {
                            Color(0xFF482625)
                        } else {
                            Color(0xFFFFE5E5)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
            )
        }
    }
}

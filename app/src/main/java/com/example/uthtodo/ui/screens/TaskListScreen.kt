package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*

@Composable
fun TaskListScreen(
    feed: TaskFeed,
    content: TaskContent,
    dashboard: DashboardContent,
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onToggle: (String) -> Unit,
    onRetry: () -> Unit
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf("")
    }

    val visible = feed.tasks.filter {
        (category.isEmpty() || it.category.name == category) &&
                (
                        it.title.contains(query.trim(), ignoreCase = true) ||
                                it.note.contains(query.trim(), ignoreCase = true)
                        )
    }

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            DashboardBottomBar(
                content = dashboard,
                selected = DashboardIcon.LIST,
                onHome = onHome,
                onAdd = onAdd
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    content.listTitle,
                    Modifier.weight(1f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    content.pendingCount(feed.tasks),
                    fontSize = 11.sp,
                    color = TodoColors.Gray
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(content.search) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = category.isEmpty(),
                    onClick = { category = "" },
                    label = { Text(content.all) }
                )

                TodoCategory.entries.forEach { item ->
                    FilterChip(
                        selected = category == item.name,
                        onClick = { category = item.name },
                        label = { Text(item.title) }
                    )
                }
            }

            TaskSyncBanner(feed, content, onRetry)

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
                            if (feed.tasks.isEmpty()) {
                                content.empty
                            } else {
                                content.noResults
                            },
                            Modifier.padding(vertical = 24.dp),
                            color = TodoColors.Gray
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
                                title,
                                Modifier.padding(top = 12.dp),
                                color = TodoColors.Gray,
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
                                showDate = true
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
    onRetry: () -> Unit
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
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (feed.isLoading) {
                CircularProgressIndicator(
                    Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )

                Spacer(Modifier.width(8.dp))
            }

            Text(
                message,
                Modifier.weight(1f),
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

@Composable
fun TaskRow(
    task: TodoTask,
    content: DashboardContent,
    onToggle: (String) -> Unit,
    showDate: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                Color.White,
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
            Modifier
                .size(48.dp)
                .toggleable(
                    value = task.isCompleted,
                    role = Role.Checkbox,
                    onValueChange = { onToggle(task.id) }
                )
                .semantics {
                    contentDescription = task.title + ": " +
                            if (task.isCompleted) {
                                content.markPending
                            } else {
                                content.markDone
                            }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .background(
                        if (task.isCompleted) {
                            TodoColors.Blue
                        } else {
                            Color.White
                        },
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (task.isCompleted) {
                            TodoColors.Blue
                        } else {
                            TodoColors.Dot
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text(
                        "✓",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                fontSize = 13.sp,
                color = if (task.isCompleted) {
                    Color(0xFF98989D)
                } else {
                    TodoColors.Ink
                },
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val study = task.category == TodoCategory.STUDY

                Text(
                    task.category.title,
                    fontSize = 10.sp,
                    color = if (study) {
                        TodoColors.Blue
                    } else {
                        Color(0xFF9B602C)
                    },
                    modifier = Modifier
                        .background(
                            if (study) {
                                TodoColors.Pale
                            } else {
                                Color(0xFFFFEEDC)
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    if (showDate) {
                        "${TaskDates.date(task.dueAtMillis)}, ${task.time}"
                    } else {
                        task.time
                    },
                    color = TodoColors.Gray,
                    fontSize = 10.sp
                )
            }
        }

        if (
            task.priority == TaskPriority.HIGH &&
            !task.isCompleted
        ) {
            Text(
                task.priority.title,
                fontSize = 10.sp,
                color = Color(0xFFCF3024),
                modifier = Modifier
                    .background(
                        Color(0xFFFFE5E5),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
            )
        }
    }
}
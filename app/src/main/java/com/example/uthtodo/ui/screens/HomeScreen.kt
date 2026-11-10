package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*

@Composable
fun HomeScreen(
    user: AppUser?,
    feed: TaskFeed,
    content: DashboardContent,
    taskContent: TaskContent,
    onProfile: () -> Unit,
    onList: () -> Unit,
    onAdd: () -> Unit,
    onRetry: () -> Unit,
    onToggleTask: (String) -> Unit,
    apiMode: Boolean = false
) {

    val tasks = if (apiMode) {
        feed.tasks
    } else {
        feed.tasks.filter {
            TaskDates.isToday(it)
        }
    }


    val stats = TaskStatistics.from(tasks)

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            DashboardBottomBar(
                content = content,
                onList = onList,
                onAdd = onAdd
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .clickable(
                            onClickLabel = content.openProfile,
                            onClick = onProfile
                        )
                ) {
                    UserAvatar(user, content.guestInitials)
                }

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        content.todayDate(),
                        fontSize = 12.sp,
                        color = TodoColors.Gray
                    )

                    Text(
                        content.greeting(user),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    Modifier
                        .size(42.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    DashboardSymbol(
                        DashboardIcon.BELL,
                        TodoColors.Ink
                    )
                }
            }

            TaskSyncBanner(feed, taskContent, onRetry)

            Spacer(Modifier.height(22.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        TodoColors.Blue,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (apiMode) "Tiến độ công việc" else content.todayProgress,
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Text(
                        content.progress(stats),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        content.encouragement,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Box(
                    Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { stats.fraction },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        strokeWidth = 6.dp,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Text(
                        "${stats.percent}%",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TodoCategory.entries.forEach { category ->
                    val study = category == TodoCategory.STUDY

                    Column(
                        Modifier
                            .weight(1f)
                            .background(
                                Color.White,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Box(
                            Modifier
                                .background(
                                    if (study) {
                                        TodoColors.Pale
                                    } else {
                                        Color(0xFFFFEEDC)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(6.dp)
                        ) {
                            DashboardSymbol(
                                if (study) {
                                    DashboardIcon.STUDY
                                } else {
                                    DashboardIcon.LIFE
                                },
                                if (study) {
                                    TodoColors.Blue
                                } else {
                                    Color(0xFFBF6B25)
                                }
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            category.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            content.remaining(
                                tasks.count {
                                    it.category == category &&
                                            !it.isCompleted
                                }
                            ),
                            color = TodoColors.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (apiMode) "Công việc từ API" else content.todayTasks,
                    modifier = Modifier.weight(1f),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onList) {
                    Text(content.viewAll, fontSize = 12.sp)
                }
            }

            if (
                tasks.isEmpty() &&
                !feed.isLoading &&
                feed.error == null
            ) {
                Text(
                    taskContent.emptyToday,
                    color = TodoColors.Gray,
                    fontSize = 13.sp
                )
            }

            tasks.take(4).forEach { task ->
                TaskRow(task, content, onToggleTask)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
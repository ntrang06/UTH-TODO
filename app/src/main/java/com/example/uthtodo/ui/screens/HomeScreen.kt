
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
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
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

    val background = TodoColors.Background
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue
    val dark = TodoColors.isDark

    Scaffold(
        containerColor = background,
        bottomBar = {
            DashboardBottomBar(
                content = content,
                selected = DashboardIcon.HOME,
                onHome = {},
                onList = onList,
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // AVATAR + LỜI CHÀO + CHUÔNG
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            onClickLabel = content.openProfile,
                            onClick = onProfile
                        )
                ) {
                    UserAvatar(
                        user = user,
                        fallback = content.guestInitials
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = content.todayDate(),
                        fontSize = 12.sp,
                        color = gray
                    )

                    Text(
                        text = content.greeting(user),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(surface)
                        .clickable(
                            onClickLabel = "Mở thông báo",
                            onClick = onNotifications
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    DashboardSymbol(
                        icon = DashboardIcon.BELL,
                        color = if (dark) {
                            TodoColors.Gray
                        } else {
                            primary
                        }
                    )
                }
            }

            TaskSyncBanner(
                feed = feed,
                content = taskContent,
                onRetry = onRetry
            )

            Spacer(Modifier.height(22.dp))

            // THẺ TIẾN ĐỘ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primary,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (apiMode) {
                            "Tiến độ công việc"
                        } else {
                            content.todayProgress
                        },
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = content.progress(stats),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = content.encouragement,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { stats.fraction },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        strokeWidth = 6.dp,
                        trackColor = Color.White.copy(
                            alpha = 0.3f
                        )
                    )

                    Text(
                        text = "${stats.percent}%",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // THẺ HỌC TẬP VÀ CUỘC SỐNG
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                TodoCategory.entries.forEach { category ->
                    val study =
                        category == TodoCategory.STUDY

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                surface,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (study) {
                                        TodoColors.Pale
                                    } else if (dark) {
                                        Color(0xFF38261B)
                                    } else {
                                        Color(0xFFFFEEDC)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(6.dp)
                        ) {
                            DashboardSymbol(
                                icon = if (study) {
                                    DashboardIcon.STUDY
                                } else {
                                    DashboardIcon.LIFE
                                },
                                color = if (study) {
                                    primary
                                } else if (dark) {
                                    Color(0xFFFFAC63)
                                } else {
                                    Color(0xFFBF6B25)
                                }
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = category.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ink
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = content.remaining(
                                tasks.count {
                                    it.category == category &&
                                            !it.isCompleted
                                }
                            ),
                            color = gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // TIÊU ĐỀ DANH SÁCH CÔNG VIỆC
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (apiMode) {
                        "Việc hôm nay"
                    } else {
                        content.todayTasks
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink
                )

                TextButton(
                    onClick = onList
                ) {
                    Text(
                        text = content.viewAll,
                        fontSize = 12.sp,
                        color = primary
                    )
                }
            }

            if (
                tasks.isEmpty() &&
                !feed.isLoading &&
                feed.error == null
            ) {
                Text(
                    text = taskContent.emptyToday,
                    color = gray,
                    fontSize = 13.sp
                )
            }

            // CÔNG VIỆC HÔM NAY
            tasks.take(4).forEach { task ->
                TaskRow(
                    task = task,
                    content = content,
                    onToggle = onToggleTask
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


package com.example.uthtodo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*

@Composable
fun ProfileScreen(
    user: AppUser?,
    tasks: List<TodoTask>,
    content: ProfileContent,
    isLoggingOut: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    BackHandler(enabled = isLoggingOut) {
        // Chờ thao tác đăng xuất kết thúc.
    }

    val stats = TaskStatistics.from(tasks)

    Scaffold(
        containerColor = DashboardBackground,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onLogout,
                    enabled = !isLoggingOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        DashboardSymbol(
                            icon = DashboardIcon.LOGOUT,
                            color = if (user == null) {
                                TodoColors.Blue
                            } else {
                                Color(0xFFDB3B24)
                            }
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = when {
                            isLoggingOut -> content.loggingOut
                            user == null -> content.login
                            else -> content.logout
                        },
                        color = if (user == null) {
                            TodoColors.Blue
                        } else {
                            Color(0xFFDB3B24)
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = content.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                TextButton(
                    onClick = onBack,
                    enabled = !isLoggingOut,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    DashboardSymbol(
                        icon = DashboardIcon.BACK,
                        color = TodoColors.Blue,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = content.back,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            UserAvatar(
                user = user,
                fallback = "UT",
                size = 82.dp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = user?.displayName ?: content.guestName,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (user == null) {
                    content.guestDescription
                } else {
                    user.email.ifBlank {
                        content.noEmail
                    }
                },
                fontSize = 12.sp,
                color = TodoColors.Gray,
                textAlign = TextAlign.Center
            )

            if (user != null) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "✓ ${content.googleLinked}",
                    fontSize = 10.sp,
                    color = Color(0xFF16805D),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFDCF5EB),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                )
            }

            Spacer(Modifier.height(20.dp))

            // Thống kê công việc
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val items = listOf(
                    stats.completed.toString() to content.completed,
                    stats.pending.toString() to content.pending,
                    "${stats.percent}%" to content.completion
                )

                items.forEach { (value, label) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 14.dp
                            )
                    ) {
                        Text(
                            text = value,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = TodoColors.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Biểu đồ công việc trong tuần
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = content.weekTitle,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = content.weekDescription,
                        fontSize = 10.sp,
                        color = TodoColors.Gray
                    )
                }

                Spacer(Modifier.height(16.dp))

                val days = content.weekProgress(tasks)

                val maxCompleted = days
                    .maxOf { it.completed }
                    .coerceAtLeast(1)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEach { day ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(64.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                val barHeight = (
                                        6f +
                                                day.completed.toFloat() /
                                                maxCompleted * 58f
                                        ).dp

                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(barHeight)
                                        .background(
                                            color = if (day.isToday) {
                                                TodoColors.Blue
                                            } else {
                                                TodoColors.Line
                                            },
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = day.label,
                                fontSize = 10.sp,
                                color = TodoColors.Gray
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

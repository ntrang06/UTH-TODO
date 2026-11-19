
package com.example.uthtodo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.uthtodo.data.SettingsRepository
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*
import kotlinx.coroutines.launch

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
        // Không cho quay lại khi đang đăng xuất
    }

    val stats = TaskStatistics.from(tasks)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settingsRepository = remember(context) {
        SettingsRepository(context.applicationContext)
    }

    val notificationsEnabled by
    settingsRepository.notificationsEnabled.collectAsState(
        initial = true
    )

    val background = TodoColors.Background
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue
    val line = TodoColors.Line
    val dark = TodoColors.isDark

    val logoutColor = if (user == null) {
        primary
    } else {
        if (dark) Color(0xFFFF766B)
        else Color(0xFFDB3B24)
    }

    Scaffold(
        containerColor = background,

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background)
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                TextButton(
                    onClick = onLogout,
                    enabled = !isLoggingOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = surface,
                        contentColor = logoutColor
                    )
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = logoutColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        DashboardSymbol(
                            icon = DashboardIcon.LOGOUT,
                            color = logoutColor
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = when {
                            isLoggingOut -> content.loggingOut
                            user == null -> content.login
                            else -> content.logout
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = logoutColor
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

            // THANH TIÊU ĐỀ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = content.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ink
                )

                TextButton(
                    onClick = onBack,
                    enabled = !isLoggingOut,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 0.dp
                    )
                ) {
                    DashboardSymbol(
                        icon = DashboardIcon.BACK,
                        color = primary,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(Modifier.width(3.dp))

                    Text(
                        text = content.back,
                        fontSize = 12.sp,
                        color = primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // AVATAR
            UserAvatar(
                user = user,
                fallback = "UT",
                size = 82.dp
            )

            Spacer(Modifier.height(12.dp))

            // TÊN NGƯỜI DÙNG
            Text(
                text = user?.displayName
                    ?: content.guestName,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = ink
            )

            Spacer(Modifier.height(4.dp))

            // EMAIL
            Text(
                text = if (user == null) {
                    content.guestDescription
                } else {
                    user.email.ifBlank {
                        content.noEmail
                    }
                },
                fontSize = 12.sp,
                color = gray,
                textAlign = TextAlign.Center
            )

            // LIÊN KẾT GOOGLE
            if (user != null) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "✓ ${content.googleLinked}",
                    fontSize = 10.sp,
                    color = if (dark) {
                        Color(0xFF79D9B2)
                    } else {
                        Color(0xFF16805D)
                    },
                    modifier = Modifier
                        .background(
                            color = if (dark) {
                                Color(0xFF193B31)
                            } else {
                                Color(0xFFDCF5EB)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                )
            }

            Spacer(Modifier.height(20.dp))

            // BA THẺ THỐNG KÊ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                val items = listOf(
                    stats.completed.toString() to
                            content.completed,
                    stats.pending.toString() to
                            content.pending,
                    "${stats.percent}%" to
                            content.completion
                )

                items.forEach { (value, label) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = surface,
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
                            fontWeight = FontWeight.Bold,
                            color = ink
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // BIỂU ĐỒ TUẦN
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = surface,
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
                        fontWeight = FontWeight.SemiBold,
                        color = ink
                    )

                    Text(
                        text = content.weekDescription,
                        fontSize = 10.sp,
                        color = gray
                    )
                }

                Spacer(Modifier.height(16.dp))

                val days = content.weekProgress(tasks)

                val maxCompleted = days
                    .maxOfOrNull { it.completed }
                    ?.coerceAtLeast(1) ?: 1

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEach { day ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(64.dp)
                                    .fillMaxWidth(),
                                contentAlignment =
                                    Alignment.BottomCenter
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
                                                primary
                                            } else {
                                                line
                                            },
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = day.label,
                                fontSize = 10.sp,
                                color = gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // CÀI ĐẶT ỨNG DỤNG TRONG HỒ SƠ
            // Giữ công tắc DataStore đã có
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Cài đặt ứng dụng",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ink
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Bật thông báo",
                            fontSize = 13.sp,
                            color = ink
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Lưu trạng thái bằng DataStore",
                            fontSize = 11.sp,
                            color = gray
                        )
                    }

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                settingsRepository
                                    .setNotificationsEnabled(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF35C56A),
                            checkedThumbColor = Color.White,
                            uncheckedTrackColor = if (dark) {
                                Color(0xFF49494F)
                            } else {
                                Color(0xFFD3D3D9)
                            },
                            uncheckedThumbColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

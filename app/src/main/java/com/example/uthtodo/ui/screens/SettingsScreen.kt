
package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.data.AppSettings
import com.example.uthtodo.data.SettingsRepository
import com.example.uthtodo.model.AppUser
import com.example.uthtodo.model.DashboardContent
import com.example.uthtodo.ui.components.*
import kotlinx.coroutines.launch

private val AccentColors = listOf(
    Color(0xFF315BEF),
    Color(0xFF128866),
    Color(0xFFD34B16),
    Color(0xFF8039F6)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    dashboard: DashboardContent,
    user: AppUser?,
    onProfile: () -> Unit,
    onHome: () -> Unit,
    onList: () -> Unit,
    onAdd: () -> Unit,
    onNotifications: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember(context) {
        SettingsRepository(context.applicationContext)
    }

    val settings by repository.settings.collectAsState(
        initial = AppSettings()
    )

    val primary = TodoColors.Blue
    val background = TodoColors.Background
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val line = TodoColors.Line

    var localFontScale by remember {
        mutableFloatStateOf(settings.fontScale)
    }

    LaunchedEffect(settings.fontScale) {
        localFontScale = settings.fontScale
    }

    Scaffold(
        containerColor = background,
        bottomBar = {
            DashboardBottomBar(
                content = dashboard,
                selected = DashboardIcon.SETTINGS,
                onHome = onHome,
                onList = onList,
                onAdd = onAdd,
                onNotifications = onNotifications,
                onSettings = {}
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(22.dp))

            // TIÊU ĐỀ VÀ AVATAR TÀI KHOẢN
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cài đặt",
                    modifier = Modifier.weight(1f),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink
                )

                // Avatar dùng chung với ProfileScreen
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClickLabel = "Mở hồ sơ cá nhân",
                            onClick = onProfile
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(
                        user = user,
                        fallback = "UT",
                        size = 44.dp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("GIAO DIỆN")

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {

                // SÁNG / TỐI / TỰ ĐỘNG
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "light" to "Sáng",
                        "dark" to "Tối",
                        "auto" to "Tự động"
                    ).forEach { (mode, label) ->
                        ThemeChoice(
                            title = label,
                            mode = mode,
                            selected = settings.themeMode == mode,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    repository.setThemeMode(mode)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = line)
                Spacer(Modifier.height(16.dp))

                // MÀU CHỦ ĐẠO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Màu chủ đạo",
                        fontSize = 13.sp,
                        color = ink,
                        modifier = Modifier.weight(1f)
                    )

                    AccentColors.forEachIndexed { index, color ->
                        AccentCircle(
                            color = color,
                            selected = settings.accentColor == index,
                            onClick = {
                                scope.launch {
                                    repository.setAccentColor(index)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = line)
                Spacer(Modifier.height(12.dp))

                // CỠ CHỮ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cỡ chữ",
                        fontSize = 13.sp,
                        color = ink,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "A",
                        fontSize = 11.sp,
                        color = gray
                    )

                    Slider(
                        value = localFontScale,
                        onValueChange = {
                            localFontScale = it
                        },
                        onValueChangeFinished = {
                            scope.launch {
                                repository.setFontScale(
                                    localFontScale
                                )
                            }
                        },
                        valueRange = 0.85f..1.25f,
                        modifier = Modifier.width(100.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = primary,
                            activeTrackColor = primary,
                            inactiveTrackColor = line
                        ),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        Color.White,
                                        CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = line,
                                        shape = CircleShape
                                    )
                            )
                        }
                    )

                    Text(
                        text = "A",
                        fontSize = 18.sp,
                        color = gray
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("THÔNG BÁO")
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                SettingsToggleRow(
                    title = "Nhắc việc",
                    subtitle = "Báo trước hạn chót 15 phút",
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            repository.setNotificationsEnabled(
                                enabled
                            )
                        }
                    }
                )

                HorizontalDivider(color = line)

                SettingsToggleRow(
                    title = "Âm thanh",
                    subtitle = "Phát âm khi có thông báo",
                    checked = settings.soundEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            repository.setSoundEnabled(
                                enabled
                            )
                        }
                    }
                )

                HorizontalDivider(color = line)

                SettingsToggleRow(
                    title = "Tổng kết cuối ngày",
                    subtitle = "Gửi lúc 21:00 mỗi tối",
                    checked = settings.dailySummaryEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            repository.setDailySummaryEnabled(
                                enabled
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(22.dp))

            SectionTitle("CHUNG")
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        surface,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ngôn ngữ",
                    fontSize = 13.sp,
                    color = ink,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Tiếng Việt",
                    fontSize = 12.sp,
                    color = gray
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "›",
                    fontSize = 21.sp,
                    color = gray
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = TodoColors.Gray
    )
}

@Composable
private fun ThemeChoice(
    title: String,
    mode: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val darkPreview = mode == "dark"
    val primary = TodoColors.Blue

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clip(shape)
                .background(
                    if (darkPreview) {
                        Color(0xFF111113)
                    } else {
                        Color(0xFFF3F3F7)
                    }
                )
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) {
                        primary
                    } else {
                        Color(0xFFE5E5EB)
                    },
                    shape = shape
                )
                .padding(9.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(37.dp)
                        .height(6.dp)
                        .background(
                            if (darkPreview) {
                                Color(0xFF66666C)
                            } else {
                                Color(0xFFD4D4D9)
                            },
                            RoundedCornerShape(4.dp)
                        )
                )

                repeat(2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .background(
                                if (darkPreview) {
                                    Color(0xFF303033)
                                } else {
                                    Color.White
                                },
                                RoundedCornerShape(5.dp)
                            )
                    )
                }
            }

            if (mode == "auto") {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .fillMaxWidth(0.46f)
                        .background(
                            Color(0xFF171719),
                            RoundedCornerShape(5.dp)
                        )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            color = if (selected) {
                primary
            } else {
                TodoColors.Gray
            }
        )
    }
}

@Composable
private fun AccentCircle(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 9.dp)
            .size(27.dp)
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) {
                    color
                } else {
                    Color.Transparent
                },
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = TodoColors.Ink,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TodoColors.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF35C56A),
                checkedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD3D3D9),
                uncheckedThumbColor = Color.White
            )
        )
    }
}


package com.example.uthtodo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.DashboardContent
import com.example.uthtodo.ui.components.*
import com.example.uthtodo.viewmodel.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun NotificationScreen(
    notifications: List<TodoNotification>,
    dashboard: DashboardContent,
    onMarkAllRead: () -> Unit,
    onDeleteNotification: (Int) -> Unit,
    onAddTask: () -> Unit,
    onSettings: () -> Unit,
    onHome: () -> Unit,
    onList: () -> Unit
) {
    val background = TodoColors.Background
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue
    val line = TodoColors.Line

    Scaffold(
        containerColor = background,
        bottomBar = {
            DashboardBottomBar(
                content = dashboard,
                selected = DashboardIcon.BELL,
                onHome = onHome,
                onList = onList,
                onAdd = onAddTask,
                onNotifications = {},
                onSettings = onSettings
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TIÊU ĐỀ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 16.dp,
                        top = 18.dp,
                        bottom = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông báo",
                    modifier = Modifier.weight(1f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink
                )

                if (notifications.isNotEmpty()) {
                    TextButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.height(40.dp),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = "Đánh dấu đã đọc",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primary,
                            maxLines = 1
                        )
                    }
                }
            }

            if (notifications.isEmpty()) {
                EmptyNotificationView(
                    modifier = Modifier.weight(1f),
                    onAddTask = onAddTask,
                    onSettings = onSettings
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 12.dp
                    )
                ) {
                    itemsIndexed(
                        items = notifications,
                        key = { _, notification ->
                            notification.id
                        }
                    ) { index, notification ->

                        val shape = when {
                            notifications.size == 1 ->
                                RoundedCornerShape(14.dp)

                            index == 0 ->
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp
                                )

                            index == notifications.lastIndex ->
                                RoundedCornerShape(
                                    bottomStart = 14.dp,
                                    bottomEnd = 14.dp
                                )

                            else ->
                                RoundedCornerShape(0.dp)
                        }

                        SwipeNotificationCard(
                            notification = notification,
                            shape = shape,
                            onDelete = {
                                onDeleteNotification(
                                    notification.id
                                )
                            }
                        )

                        if (index != notifications.lastIndex) {
                            HorizontalDivider(
                                color = line,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeNotificationCard(
    notification: TodoNotification,
    shape: RoundedCornerShape,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val revealWidth = with(LocalDensity.current) {
        64.dp.toPx()
    }

    val offsetX = remember(notification.id) {
        Animatable(0f)
    }

    val dragState = rememberDraggableState { delta ->
        scope.launch {
            offsetX.snapTo(
                (offsetX.value + delta)
                    .coerceIn(-revealWidth, 0f)
            )
        }
    }

    val dark = TodoColors.isDark
    val surface = TodoColors.Surface
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray
    val primary = TodoColors.Blue

    val icon = when (notification.type) {
        NotificationType.UPCOMING ->
            Icons.Outlined.Alarm

        NotificationType.OVERDUE ->
            Icons.Outlined.WarningAmber

        NotificationType.COMPLETED ->
            Icons.Outlined.Check

        NotificationType.REMINDER ->
            Icons.Outlined.Alarm
    }

    val iconColor = when (notification.type) {
        NotificationType.UPCOMING,
        NotificationType.REMINDER ->
            primary

        NotificationType.OVERDUE ->
            if (dark) {
                Color(0xFFFF9284)
            } else {
                Color(0xFFC96555)
            }

        NotificationType.COMPLETED ->
            if (dark) {
                Color(0xFF70D6AA)
            } else {
                Color(0xFF16805D)
            }
    }

    val iconBackground = when (notification.type) {
        NotificationType.UPCOMING,
        NotificationType.REMINDER ->
            if (dark) {
                Color(0xFF293452)
            } else {
                Color(0xFFEAF0FF)
            }

        NotificationType.OVERDUE ->
            if (dark) {
                Color(0xFF482B2B)
            } else {
                Color(0xFFFFEDE9)
            }

        NotificationType.COMPLETED ->
            if (dark) {
                Color(0xFF213D33)
            } else {
                Color(0xFFE2F5ED)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        // NÚT XÓA ẨN PHÍA SAU
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(
                    if (dark) {
                        Color(0xFF522B2B)
                    } else {
                        Color(0xFFFBE4E1)
                    }
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight()
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Xóa thông báo",
                    tint = if (dark) {
                        Color(0xFFFF9B90)
                    } else {
                        Color(0xFFD64C41)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // DÒNG THÔNG BÁO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt(),
                        0
                    )
                }
                .background(surface)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = dragState,
                    onDragStopped = {
                        scope.launch {
                            val target = if (
                                offsetX.value < -revealWidth / 2f
                            ) {
                                -revealWidth
                            } else {
                                0f
                            }

                            offsetX.animateTo(target)
                        }
                    }
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ICON PASTEL
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            // NỘI DUNG
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(6.dp))

            // THỜI GIAN VÀ DẤU CHẤM CHƯA ĐỌC
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = notification.time,
                    fontSize = 9.sp,
                    color = gray,
                    maxLines = 1
                )

                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                primary,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationView(
    modifier: Modifier = Modifier,
    onAddTask: () -> Unit,
    onSettings: () -> Unit
) {
    val dark = TodoColors.isDark
    val primary = TodoColors.Blue
    val ink = TodoColors.Ink
    val gray = TodoColors.Gray

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // VÒNG TRÒN CHUÔNG
            Box(
                modifier = Modifier.size(124.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(
                            if (dark) {
                                Color(0xFF293452)
                            } else {
                                Color(0xFFE8EDFF)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                TodoColors.Surface,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // DẤU TÍCH XANH
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(
                            x = (-5).dp,
                            y = (-5).dp
                        )
                        .size(30.dp)
                        .background(
                            Color(0xFF49A76A),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Chưa có thông báo nào",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ink,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Khi có việc sắp đến hạn, quá hạn hoặc\n" +
                        "lời nhắc, bạn sẽ thấy chúng ở đây.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onAddTask,
                modifier = Modifier.height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(
                    horizontal = 20.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Thêm việc cần làm",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = onSettings) {
                Text(
                    text = "Cài đặt nhắc việc",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primary
                )
            }
        }
    }
}

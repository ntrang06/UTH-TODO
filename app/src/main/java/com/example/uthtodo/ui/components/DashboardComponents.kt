package com.example.uthtodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.AppUser
import com.example.uthtodo.model.DashboardContent

val DashboardBackground = Color(0xFFF5F5F8)

enum class DashboardIcon {
    HOME, STUDY, LIFE, LIST, BELL, SETTINGS, PLUS, BACK, LOGOUT
}

@Composable
fun DashboardSymbol(
    icon: DashboardIcon,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(24.dp)) {
        withTransform({
            scale(size.width / 24f, size.height / 24f, Offset.Zero)
        }) {
            val stroke = Stroke(width = 1.6f, cap = StrokeCap.Round)

            fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
                drawLine(
                    color,
                    Offset(x1, y1),
                    Offset(x2, y2),
                    1.6f,
                    StrokeCap.Round
                )
            }

            fun path(points: List<Offset>, closed: Boolean = false) {
                drawPath(
                    Path().apply {
                        moveTo(points[0].x, points[0].y)
                        points.drop(1).forEach {
                            lineTo(it.x, it.y)
                        }
                        if (closed) close()
                    },
                    color,
                    style = stroke
                )
            }

            when (icon) {
                DashboardIcon.HOME, DashboardIcon.LIFE -> {
                    path(
                        listOf(
                            Offset(3f, 11f),
                            Offset(12f, 3f),
                            Offset(21f, 11f)
                        )
                    )

                    path(
                        listOf(
                            Offset(5f, 10f),
                            Offset(5f, 21f),
                            Offset(10f, 21f),
                            Offset(10f, 15f),
                            Offset(14f, 15f),
                            Offset(14f, 21f),
                            Offset(19f, 21f),
                            Offset(19f, 10f)
                        )
                    )
                }

                DashboardIcon.STUDY -> {
                    path(
                        listOf(
                            Offset(2f, 8f),
                            Offset(12f, 4f),
                            Offset(22f, 8f),
                            Offset(12f, 12f)
                        ),
                        closed = true
                    )

                    path(
                        listOf(
                            Offset(6f, 10f),
                            Offset(6f, 16f),
                            Offset(12f, 19f),
                            Offset(18f, 16f),
                            Offset(18f, 10f)
                        )
                    )

                    line(22f, 8f, 22f, 16f)
                }

                DashboardIcon.LIST -> repeat(3) { index ->
                    val y = 6f + index * 6f
                    drawCircle(color, 1f, Offset(4f, y))
                    line(8f, y, 21f, y)
                }

                DashboardIcon.BELL -> {
                    drawPath(
                        Path().apply {
                            moveTo(4f, 18f)
                            lineTo(6f, 15f)
                            lineTo(6f, 10f)
                            cubicTo(6f, 2f, 18f, 2f, 18f, 10f)
                            lineTo(18f, 15f)
                            lineTo(20f, 18f)
                            close()
                        },
                        color,
                        style = stroke
                    )

                    drawArc(
                        color,
                        0f,
                        180f,
                        false,
                        Offset(10f, 19f),
                        Size(4f, 3f),
                        style = stroke
                    )
                }

                DashboardIcon.SETTINGS -> repeat(3) { index ->
                    val y = 6f + index * 6f
                    val x = if (index == 1) 9f else 16f

                    line(3f, y, 21f, y)
                    drawCircle(Color.White, 2.3f, Offset(x, y))
                    drawCircle(color, 2.3f, Offset(x, y), style = stroke)
                }

                DashboardIcon.PLUS -> {
                    line(5f, 12f, 19f, 12f)
                    line(12f, 5f, 12f, 19f)
                }

                DashboardIcon.BACK -> path(
                    listOf(
                        Offset(15f, 5f),
                        Offset(8f, 12f),
                        Offset(15f, 19f)
                    )
                )

                DashboardIcon.LOGOUT -> {
                    path(
                        listOf(
                            Offset(10f, 4f),
                            Offset(5f, 4f),
                            Offset(5f, 20f),
                            Offset(10f, 20f)
                        )
                    )

                    line(10f, 12f, 22f, 12f)

                    path(
                        listOf(
                            Offset(18f, 8f),
                            Offset(22f, 12f),
                            Offset(18f, 16f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun UserAvatar(
    user: AppUser?,
    fallback: String,
    size: Dp = 44.dp
) {
    Box(
        Modifier
            .size(size)
            .background(TodoColors.Pale, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            user?.initials() ?: fallback,
            color = TodoColors.Blue,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.36f).sp
        )
    }
}

@Composable
fun DashboardBottomBar(
    content: DashboardContent,
    selected: DashboardIcon = DashboardIcon.HOME,
    onHome: () -> Unit = {},
    onList: () -> Unit = {},
    onAdd: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DashboardTab(
            DashboardIcon.HOME,
            content.homeTab,
            selected == DashboardIcon.HOME,
            onHome
        )

        DashboardTab(
            DashboardIcon.LIST,
            content.listTab,
            selected == DashboardIcon.LIST,
            onList
        )

        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .offset(y = (-10).dp)
                    .size(52.dp)
                    .background(TodoColors.Blue, CircleShape)
                    .clickable(
                        onClickLabel = content.addTask,
                        onClick = onAdd
                    ),
                contentAlignment = Alignment.Center
            ) {
                DashboardSymbol(DashboardIcon.PLUS, Color.White)
            }
        }

        DashboardTab(DashboardIcon.BELL, content.notificationsTab)
        DashboardTab(DashboardIcon.SETTINGS, content.settingsTab)
    }
}

@Composable
private fun RowScope.DashboardTab(
    icon: DashboardIcon,
    label: String,
    selected: Boolean = false,
    action: (() -> Unit)? = null
) {
    val color = if (selected) TodoColors.Blue else TodoColors.Gray

    Column(
        Modifier
            .weight(1f)
            .then(
                if (action != null) {
                    Modifier.clickable(onClick = action)
                } else {
                    Modifier
                }
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardSymbol(icon, color)

        Spacer(Modifier.height(3.dp))

        Text(
            label,
            color = color,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}
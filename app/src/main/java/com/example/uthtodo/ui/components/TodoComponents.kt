package com.example.uthtodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*

object TodoColors {
    val Blue = Color(0xFF2F5BEA)
    val White = Color.White
    val Ink = Color(0xFF1C1C1E)
    val Gray = Color(0xFF636366)
    val Pale = Color(0xFFEAEFFD)
    val Dot = Color(0xFFD1D1D6)
    val Line = Color(0xFFE4E4EA)
}

@Composable
fun TodoPrimaryButton(
    label: String,
    onClick: () -> Unit,
    scale: Float = 1f
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp * scale),
        shape = RoundedCornerShape(16.dp * scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = TodoColors.Blue
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        Text(
            text = label,
            color = TodoColors.White,
            fontSize = (18 * scale).sp,
            lineHeight = (24 * scale).sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TodoBrand(
    appInfo: AppInfo,
    scale: Float = 1f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodoLogo(
            logoSize = 32.dp * scale,
            description = appInfo.logoDescription
        )

        Spacer(Modifier.width(10.dp * scale))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = TodoColors.Ink,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(appInfo.name.substringBefore(' '))
                }

                withStyle(
                    SpanStyle(
                        color = TodoColors.Blue,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(" ")
                    append(appInfo.name.substringAfter(' ', ""))
                }
            },
            fontSize = (20 * scale).sp,
            lineHeight = (28 * scale).sp
        )
    }
}

@Composable
fun TodoLogo(
    logoSize: Dp,
    description: String,
    inverted: Boolean = false
) {
    Canvas(
        Modifier
            .size(logoSize)
            .semantics {
                contentDescription = description
            }
    ) {
        val w = size.width

        val ink = if (inverted) {
            TodoColors.Blue
        } else {
            TodoColors.White
        }

        val paper = if (inverted) {
            TodoColors.White
        } else {
            TodoColors.Blue
        }

        drawRoundRect(
            color = paper,
            cornerRadius = CornerRadius(w * .23f)
        )

        drawCircle(
            color = ink,
            radius = w * .09f,
            center = Offset(w * .31f, w * .37f)
        )

        drawLine(
            color = paper,
            start = Offset(w * .275f, w * .37f),
            end = Offset(w * .30f, w * .395f),
            strokeWidth = w * .018f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = paper,
            start = Offset(w * .30f, w * .395f),
            end = Offset(w * .35f, w * .34f),
            strokeWidth = w * .018f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = ink.copy(alpha = .55f),
            radius = w * .075f,
            center = Offset(w * .31f, w * .63f),
            style = Stroke(w * .032f)
        )

        drawLine(
            color = ink,
            start = Offset(w * .48f, w * .37f),
            end = Offset(w * .73f, w * .37f),
            strokeWidth = w * .075f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = ink.copy(alpha = .55f),
            start = Offset(w * .48f, w * .63f),
            end = Offset(w * .65f, w * .63f),
            strokeWidth = w * .075f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TodoIllustration(
    type: IllustrationType,
    data: IllustrationData,
    artHeight: Dp,
    scale: Float = 1f
) {
    Box(
        modifier = Modifier
            .width(220.dp * scale)
            .height(artHeight)
            .background(
                TodoColors.Pale,
                RoundedCornerShape(28.dp * scale)
            )
            .padding(horizontal = 10.dp * scale),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            IllustrationType.TASKS -> {
                Column(
                    modifier = Modifier.offset(y = 6.dp * scale),
                    verticalArrangement = Arrangement.spacedBy(
                        12.dp * scale
                    )
                ) {
                    data.tasks.forEachIndexed { index, task ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp * scale)
                                .semantics {
                                    contentDescription = task.title
                                },
                            color = TodoColors.White,
                            shape = RoundedCornerShape(16.dp * scale),
                            shadowElevation = 5.dp * scale
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 14.dp * scale
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Canvas(
                                    Modifier.size(22.dp * scale)
                                ) {
                                    if (task.isDone) {
                                        drawCircle(TodoColors.Blue)

                                        drawLine(
                                            color = TodoColors.White,
                                            start = Offset(
                                                size.width * .27f,
                                                size.height * .50f
                                            ),
                                            end = Offset(
                                                size.width * .44f,
                                                size.height * .66f
                                            ),
                                            strokeWidth = 1.7.dp.toPx() * scale,
                                            cap = StrokeCap.Round
                                        )

                                        drawLine(
                                            color = TodoColors.White,
                                            start = Offset(
                                                size.width * .44f,
                                                size.height * .66f
                                            ),
                                            end = Offset(
                                                size.width * .74f,
                                                size.height * .34f
                                            ),
                                            strokeWidth = 1.7.dp.toPx() * scale,
                                            cap = StrokeCap.Round
                                        )
                                    } else {
                                        drawCircle(
                                            color = Color(0xFFC7C7CC),
                                            radius = size.minDimension / 2 -
                                                    1.dp.toPx() * scale,
                                            style = Stroke(
                                                2.dp.toPx() * scale
                                            )
                                        )
                                    }
                                }

                                Spacer(
                                    Modifier.width(12.dp * scale)
                                )

                                val length = when (index) {
                                    0 -> 110
                                    1 -> 90
                                    else -> 120
                                }

                                Box(
                                    Modifier
                                        .width(length.dp * scale)
                                        .height(8.dp * scale)
                                        .background(
                                            TodoColors.Line,
                                            RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            IllustrationType.CATEGORIES -> {
                Column(
                    modifier = Modifier.offset(y = 6.dp * scale),
                    verticalArrangement = Arrangement.spacedBy(
                        14.dp * scale
                    )
                ) {
                    data.categories.forEachIndexed { index, category ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = TodoColors.White,
                            shape = RoundedCornerShape(16.dp * scale),
                            shadowElevation = 5.dp * scale
                        ) {
                            Column(
                                Modifier.padding(16.dp * scale)
                            ) {
                                Text(
                                    text = category.name,
                                    color = if (index == 0) {
                                        TodoColors.Blue
                                    } else {
                                        Color(0xFF9C4A13)
                                    },
                                    fontSize = (12 * scale).sp,
                                    lineHeight = (16 * scale).sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .background(
                                            color = if (index == 0) {
                                                Color(0xFFE6EDFF)
                                            } else {
                                                Color(0xFFFFEBD9)
                                            },
                                            shape = RoundedCornerShape(
                                                6.dp * scale
                                            )
                                        )
                                        .padding(
                                            horizontal = 10.dp * scale,
                                            vertical = 3.dp * scale
                                        )
                                )

                                Spacer(
                                    Modifier.height(8.dp * scale)
                                )

                                Box(
                                    Modifier
                                        .width(
                                            (if (index == 0) {
                                                140.dp
                                            } else {
                                                110.dp
                                            }) * scale
                                        )
                                        .height(8.dp * scale)
                                        .background(
                                            TodoColors.Line,
                                            RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            IllustrationType.REMINDER -> {
                Column(
                    modifier = Modifier.offset(y = 12.dp * scale),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(84.dp * scale),
                        color = TodoColors.White,
                        shape = CircleShape,
                        shadowElevation = 8.dp * scale
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                Modifier
                                    .size(36.dp * scale)
                                    .semantics {
                                        contentDescription =
                                            data.reminder.iconDescription
                                    }
                            ) {
                                val w = size.width
                                val h = size.height

                                val bell = Path().apply {
                                    moveTo(w * .18f, h * .72f)
                                    lineTo(w * .27f, h * .59f)
                                    lineTo(w * .29f, h * .34f)

                                    cubicTo(
                                        w * .29f, h * .04f,
                                        w * .71f, h * .04f,
                                        w * .71f, h * .34f
                                    )

                                    lineTo(w * .73f, h * .59f)
                                    lineTo(w * .82f, h * .72f)
                                    close()
                                }

                                drawPath(
                                    path = bell,
                                    color = TodoColors.Blue,
                                    style = Stroke(
                                        2.4.dp.toPx() * scale
                                    )
                                )

                                drawArc(
                                    color = TodoColors.Blue,
                                    startAngle = 0f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    topLeft = Offset(
                                        w * .40f,
                                        h * .78f
                                    ),
                                    size = Size(
                                        w * .20f,
                                        h * .13f
                                    ),
                                    style = Stroke(
                                        2.4.dp.toPx() * scale
                                    )
                                )
                            }
                        }
                    }

                    Spacer(
                        Modifier.height(18.dp * scale)
                    )

                    Surface(
                        modifier = Modifier.width(180.dp * scale),
                        color = TodoColors.White,
                        shape = RoundedCornerShape(14.dp * scale),
                        shadowElevation = 6.dp * scale
                    ) {
                        Text(
                            text = data.reminder.message,
                            color = TodoColors.Ink,
                            fontSize = (14 * scale).sp,
                            lineHeight = (20 * scale).sp,
                            modifier = Modifier.padding(
                                horizontal = 14.dp * scale,
                                vertical = 10.dp * scale
                            )
                        )
                    }
                }
            }
        }
    }
}
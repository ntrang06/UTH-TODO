package com.example.uthtodo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.TodoColors
import com.example.uthtodo.ui.components.TodoLogo

@Composable
fun LoginScreen(
    appInfo: AppInfo,
    content: LoginContent,
    uiState: LoginUiState,
    onGoogleClick: () -> Unit,
    onGuestClick: () -> Unit,
    onBack: () -> Unit,
    onDismissError: () -> Unit
) {
    BackHandler(enabled = uiState.isLoading) { }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TodoColors.White)
            .safeDrawingPadding()
    ) {
        val scale = (maxWidth / 390.dp).coerceIn(.8f, 1.2f)

        val fontScale = LocalDensity.current.fontScale
            .coerceAtLeast(1f)

        val canvasHeight = maxOf(
            maxHeight,
            800.dp * scale * fontScale * fontScale
        )

        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
                    .padding(horizontal = 28.dp * scale)
                    .padding(
                        top = 24.dp * scale,
                        bottom = 32.dp * scale
                    )
            ) {
                // Nút quay lại.
                IconButton(
                    onClick = onBack,
                    enabled = !uiState.isLoading
                ) {
                    Canvas(
                        Modifier
                            .size(20.dp * scale)
                            .semantics {
                                contentDescription = content.backLabel
                            }
                    ) {
                        drawLine(
                            color = TodoColors.Ink,
                            start = Offset(
                                size.width * .65f,
                                size.height * .2f
                            ),
                            end = Offset(
                                size.width * .35f,
                                size.height * .5f
                            ),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = TodoColors.Ink,
                            start = Offset(
                                size.width * .35f,
                                size.height * .5f
                            ),
                            end = Offset(
                                size.width * .65f,
                                size.height * .8f
                            ),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                Spacer(Modifier.height(112.dp * scale))

                TodoLogo(
                    logoSize = 88.dp * scale,
                    description = appInfo.logoDescription
                )

                Spacer(Modifier.height(24.dp * scale))

                Text(
                    text = content.title,
                    color = TodoColors.Ink,
                    fontSize = (30 * scale).sp,
                    lineHeight = (38 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                )

                Spacer(Modifier.height(12.dp * scale))

                Text(
                    text = content.description,
                    color = TodoColors.Gray,
                    fontSize = (16 * scale).sp,
                    lineHeight = (24 * scale).sp
                )

                Spacer(Modifier.height(24.dp * scale))

                // Hai dòng giới thiệu lợi ích.
                content.benefits.forEachIndexed { index, benefit ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            Modifier.size(18.dp * scale)
                        ) {
                            val stroke = 1.5.dp.toPx() * scale

                            if (index == 0) {
                                drawArc(
                                    color = TodoColors.Blue,
                                    startAngle = 35f,
                                    sweepAngle = 290f,
                                    useCenter = false,
                                    topLeft = Offset(
                                        size.width * .15f,
                                        size.height * .15f
                                    ),
                                    size = Size(
                                        size.width * .7f,
                                        size.height * .7f
                                    ),
                                    style = Stroke(stroke)
                                )

                                drawLine(
                                    color = TodoColors.Blue,
                                    start = Offset(
                                        size.width * .83f,
                                        size.height * .14f
                                    ),
                                    end = Offset(
                                        size.width * .83f,
                                        size.height * .42f
                                    ),
                                    strokeWidth = stroke
                                )

                                drawLine(
                                    color = TodoColors.Blue,
                                    start = Offset(
                                        size.width * .83f,
                                        size.height * .14f
                                    ),
                                    end = Offset(
                                        size.width * .55f,
                                        size.height * .14f
                                    ),
                                    strokeWidth = stroke
                                )
                            } else {
                                drawRoundRect(
                                    color = TodoColors.Blue,
                                    topLeft = Offset(
                                        size.width * .27f,
                                        size.height * .1f
                                    ),
                                    size = Size(
                                        size.width * .46f,
                                        size.height * .8f
                                    ),
                                    cornerRadius = CornerRadius(
                                        2.dp.toPx()
                                    ),
                                    style = Stroke(stroke)
                                )

                                drawCircle(
                                    color = TodoColors.Blue,
                                    radius = 1.dp.toPx(),
                                    center = Offset(
                                        size.width * .5f,
                                        size.height * .77f
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp * scale))

                        Text(
                            text = benefit,
                            color = TodoColors.Ink,
                            fontSize = (14 * scale).sp,
                            lineHeight = (22 * scale).sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(4.dp * scale))
                }

                Spacer(Modifier.weight(1f))

                // Nút đăng nhập Google.
                OutlinedButton(
                    onClick = onGoogleClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp * scale),
                    shape = RoundedCornerShape(16.dp * scale),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        TodoColors.Line
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TodoColors.Ink
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp * scale),
                            color = TodoColors.Blue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp * scale)
                                .border(
                                    1.dp,
                                    TodoColors.Line,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = TodoColors.Gray,
                                fontSize = (13 * scale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp * scale))

                    Text(
                        text = if (uiState.isLoading) {
                            content.loadingLabel
                        } else {
                            content.googleLabel
                        },
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp * scale))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TodoColors.Line
                    )

                    Text(
                        text = content.separatorLabel,
                        color = TodoColors.Gray,
                        fontSize = (12 * scale).sp,
                        modifier = Modifier.padding(
                            horizontal = 12.dp * scale
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TodoColors.Line
                    )
                }

                Spacer(Modifier.height(12.dp * scale))

                TextButton(
                    onClick = onGuestClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp * scale)
                ) {
                    Text(
                        text = content.guestLabel,
                        color = TodoColors.Blue,
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp * scale))

                Text(
                    text = content.legalText,
                    color = TodoColors.Gray,
                    fontSize = (11 * scale).sp,
                    lineHeight = (16 * scale).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Hộp thoại khi đăng nhập thất bại.
    uiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = onDismissError,
            title = {
                Text(content.errorTitle)
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text(content.dismissLabel)
                }
            }
        )
    }
}
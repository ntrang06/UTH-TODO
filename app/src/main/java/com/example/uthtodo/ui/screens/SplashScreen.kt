package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.AppInfo
import com.example.uthtodo.ui.components.TodoColors
import com.example.uthtodo.ui.components.TodoLogo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    appInfo: AppInfo,
    onFinished: () -> Unit
) {
    val latestOnFinished = rememberUpdatedState(onFinished)

    LaunchedEffect(appInfo.splashDurationMillis) {
        delay(appInfo.splashDurationMillis)
        latestOnFinished.value()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TodoColors.Blue)
            .safeDrawingPadding()
    ) {
        val scale = (maxWidth / 390.dp).coerceIn(0.8f, 1.2f)
        val viewportHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = viewportHeight * .325f)
                .padding(horizontal = 24.dp * scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TodoLogo(
                logoSize = 112.dp * scale,
                description = appInfo.logoDescription,
                inverted = true
            )

            Spacer(
                Modifier.height(20.dp * scale)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append(appInfo.name.substringBefore(' '))
                    }

                    withStyle(
                        SpanStyle(fontWeight = FontWeight.Medium)
                    ) {
                        append(" ")
                        append(appInfo.name.substringAfter(' ', ""))
                    }
                },
                color = TodoColors.White,
                fontSize = (40 * scale).sp,
                lineHeight = (48 * scale).sp
            )

            Spacer(
                Modifier.height(4.dp * scale)
            )

            Text(
                text = appInfo.slogan,
                color = TodoColors.White.copy(alpha = .92f),
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp * scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thanh ngắn giống ảnh mẫu.
            Box(
                modifier = Modifier
                    .width(120.dp * scale)
                    .height(4.dp * scale)
                    .background(
                        TodoColors.White.copy(alpha = .30f),
                        RoundedCornerShape(50)
                    )
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.4f)
                        .background(
                            TodoColors.White,
                            RoundedCornerShape(50)
                        )
                )
            }

            Spacer(
                Modifier.height(16.dp * scale)
            )

            Text(
                text = appInfo.version,
                color = TodoColors.White.copy(alpha = .90f),
                fontSize = (12 * scale).sp,
                lineHeight = (16 * scale).sp
            )
        }
    }
}
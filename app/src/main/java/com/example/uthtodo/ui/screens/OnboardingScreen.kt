package com.example.uthtodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.components.*

@Composable
fun OnboardingScreen(
    appInfo: AppInfo,
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    artwork: IllustrationData,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    key(pageIndex) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(TodoColors.White)
                .safeDrawingPadding()
        ) {
            // Tỷ lệ theo khung tham chiếu rộng 390 dp.
            val scale = (maxWidth / 390.dp).coerceIn(0.8f, 1.2f)
            val side = 28.dp * scale

            val density = LocalDensity.current
            val measurer = rememberTextMeasurer()

            val titleStyle = TextStyle(
                fontSize = (28 * scale).sp,
                lineHeight = (36 * scale).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            )

            val descriptionStyle = TextStyle(
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp,
                letterSpacing = 0.sp
            )

            val textWidth = with(density) {
                (maxWidth - side * 2).roundToPx()
            }

            val titleSize = measurer.measure(
                text = page.title,
                style = titleStyle,
                constraints = Constraints(maxWidth = textWidth)
            ).size

            val descriptionSize = measurer.measure(
                text = page.description,
                style = descriptionStyle,
                constraints = Constraints(maxWidth = textWidth)
            ).size

            val textHeight = maxOf(
                120.dp * scale,
                with(density) {
                    (titleSize.height + descriptionSize.height).toDp()
                }
            )

            // Tăng vùng cuộn khi chữ lớn để nội dung không chạm nút.
            val minimumHeight =
                (textHeight + 168.dp * scale) / (1f - 540f / 844f)

            val canvasHeight = maxOf(
                maxHeight,
                minimumHeight,
                740.dp * scale
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(canvasHeight)
                ) {
                    // Logo và Bỏ qua.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                y = canvasHeight * (58f / 844f)
                            )
                            .padding(horizontal = side)
                            .height(48.dp * scale),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TodoBrand(
                            appInfo = appInfo,
                            scale = scale
                        )

                        TextButton(
                            onClick = onSkip,
                            contentPadding = PaddingValues(
                                horizontal = 4.dp * scale
                            )
                        ) {
                            Text(
                                text = appInfo.skipLabel,
                                color = TodoColors.Gray,
                                fontSize = (14 * scale).sp
                            )
                        }
                    }

                    // Hình minh họa.
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(
                                y = canvasHeight * (190f / 844f)
                            )
                    ) {
                        TodoIllustration(
                            type = page.illustrationType,
                            data = artwork,
                            artHeight = 260.dp * scale,
                            scale = scale
                        )
                    }

                    // Nội dung: cùng vị trí ở cả ba bước.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                y = canvasHeight * (540f / 844f)
                            )
                            .padding(horizontal = side)
                    ) {
                        Text(
                            text = page.title,
                            color = TodoColors.Ink,
                            style = titleStyle
                        )

                        Spacer(
                            Modifier.height(12.dp * scale)
                        )

                        Text(
                            text = page.description,
                            color = TodoColors.Gray,
                            style = descriptionStyle
                        )
                    }

                    // Dấu chấm và nút.
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = side)
                            .padding(bottom = 40.dp * scale)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp * scale
                            )
                        ) {
                            repeat(pageCount) { index ->
                                Box(
                                    Modifier
                                        .width(
                                            (if (index == pageIndex) {
                                                24.dp
                                            } else {
                                                8.dp
                                            }) * scale
                                        )
                                        .height(8.dp * scale)
                                        .background(
                                            color = if (index == pageIndex) {
                                                TodoColors.Blue
                                            } else {
                                                TodoColors.Dot
                                            },
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            }
                        }

                        Spacer(
                            Modifier.height(28.dp * scale)
                        )

                        TodoPrimaryButton(
                            label = page.buttonText(),
                            onClick = onNext,
                            scale = scale
                        )
                    }
                }
            }
        }
    }
}
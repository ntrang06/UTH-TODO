
package com.example.uthtodo.ui.screens

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.data.TeachingTask
import com.example.uthtodo.data.TeachingTaskApi
import com.example.uthtodo.ui.components.DashboardBackground
import com.example.uthtodo.ui.components.TodoColors
import kotlinx.coroutines.CancellationException

@Composable
fun TaskDetailScreen(
    taskId: Int,
    onBack: () -> Unit
) {
    val api = remember { TeachingTaskApi() }

    var task by remember(taskId) {
        mutableStateOf<TeachingTask?>(null)
    }
    var loading by remember(taskId) {
        mutableStateOf(true)
    }
    var error by remember(taskId) {
        mutableStateOf<String?>(null)
    }
    var retry by remember(taskId) {
        mutableIntStateOf(0)
    }

    LaunchedEffect(taskId, retry) {
        loading = true
        error = null

        try {
            task = api.getTaskById(taskId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.localizedMessage
                ?: "Không tải được chi tiết công việc."
        } finally {
            loading = false
        }
    }

    Scaffold(
        containerColor = DashboardBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("‹ Trang chủ")
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "Chi tiết",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "Sửa",
                    color = TodoColors.Gray,
                    fontSize = 14.sp
                )
            }
        },
        bottomBar = {
            if (task != null && !loading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DashboardBackground)
                        .navigationBarsPadding()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            if (task?.done == true) {
                                "Đã hoàn thành"
                            } else {
                                "Đánh dấu hoàn thành"
                            }
                        )
                    }

                    TextButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Text("Xóa công việc")
                    }
                }
            }
        }
    ) { padding ->

        when {
            loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(error.orEmpty())

                    Button(onClick = { retry++ }) {
                        Text("Thử lại")
                    }
                }
            }

            task != null -> {
                val detail = task!!
                val completed = detail.subtasks.count { it.done }
                val total = detail.subtasks.size

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (detail.category == "study") {
                            "Học tập"
                        } else {
                            "Cuộc sống"
                        },
                        fontSize = 12.sp,
                        color = TodoColors.Blue
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        detail.title,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 31.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        DetailInfoRow(
                            label = "Hạn chót",
                            value = "${detail.date}, ${detail.time}"
                        )

                        HorizontalDivider(
                            Modifier.padding(vertical = 12.dp)
                        )

                        DetailInfoRow(
                            label = "Nhắc nhở",
                            value = if (detail.remind) {
                                "Đã bật"
                            } else {
                                "Đã tắt"
                            }
                        )

                        HorizontalDivider(
                            Modifier.padding(vertical = 12.dp)
                        )

                        DetailInfoRow(
                            label = "Ưu tiên",
                            value = when (detail.priority) {
                                "high" -> "Cao"
                                "low" -> "Thấp"
                                else -> "Vừa"
                            }
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "Ghi chú",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        detail.note.ifBlank { "Không có ghi chú" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Việc nhỏ",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            "$completed/$total",
                            color = TodoColors.Gray
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (total == 0) 0f
                            else completed.toFloat() / total
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = TodoColors.Blue
                    )

                    Spacer(Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (detail.subtasks.isEmpty()) {
                            Text(
                                "Chưa có việc nhỏ",
                                color = TodoColors.Gray
                            )
                        }

                        detail.subtasks.forEach { sub ->
                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sub.done,
                                    onCheckedChange = null
                                )

                                Text(
                                    text = sub.title,
                                    fontSize = 13.sp,
                                    textDecoration = if (sub.done) {
                                        TextDecoration.LineThrough
                                    } else {
                                        null
                                    },
                                    color = if (sub.done) {
                                        TodoColors.Gray
                                    } else {
                                        Color.Black
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TodoColors.Gray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

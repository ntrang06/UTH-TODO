
package com.example.uthtodo.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.data.TeachingTask
import com.example.uthtodo.data.TeachingTaskApi
import com.example.uthtodo.data.TeachingTaskPatchApi
import com.example.uthtodo.ui.components.DashboardBackground
import com.example.uthtodo.ui.components.TodoColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun TaskDetailScreen(
    taskId: Int,
    onBack: () -> Unit,
    cachedTask: TeachingTask? = null,
    onTaskUpdated: (TeachingTask) -> Unit = {}
) {
    val api = remember { TeachingTaskApi() }
    val patchApi = remember { TeachingTaskPatchApi() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

    var isUpdating by remember(taskId) {
        mutableStateOf(false)
    }

    // GET API: Lấy thông tin công việc.
    LaunchedEffect(taskId, retry) {
        loading = true
        error = null

        try {
            task = cachedTask ?: api.getTaskById(taskId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.localizedMessage
                ?: "Không tải được chi tiết công việc."
        } finally {
            loading = false
        }
    }

    // Nhận trạng thái đã cập nhật từ Navigation.
    LaunchedEffect(cachedTask) {
        if (cachedTask != null && !isUpdating) {
            task = cachedTask
        }
    }

    // Hàm PATCH dùng chung cho công việc và việc nhỏ.
    fun updateTask(
        updatedTask: TeachingTask,
        successMessage: String
    ) {
        if (isUpdating) return

        scope.launch {
            isUpdating = true

            try {
                // Gửi dữ liệu mới lên API.
                patchApi.updateTask(updatedTask)

                // Chỉ đổi giao diện khi API trả thành công.
                task = updatedTask

                // Cập nhật dữ liệu dùng chung trong Navigation.
                onTaskUpdated(updatedTask)

                Toast.makeText(
                    context,
                    successMessage,
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "PATCH thất bại: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isUpdating = false
            }
        }
    }

    // PATCH: Đánh dấu công việc chính hoàn thành.
    fun markTaskCompleted() {
        val current = task ?: return

        if (current.done || isUpdating) return

        updateTask(
            updatedTask = current.copy(done = true),
            successMessage = "Đã hoàn thành công việc!"
        )
    }

    // PATCH: Thay đổi trạng thái một việc nhỏ.
    fun toggleSubtask(
        index: Int,
        checked: Boolean
    ) {
        val current = task ?: return

        if (isUpdating) return

        if (index !in current.subtasks.indices) {
            return
        }

        val updatedSubtasks = current.subtasks.mapIndexed {
                subIndex, subtask ->

            if (subIndex == index) {
                subtask.copy(done = checked)
            } else {
                subtask
            }
        }

        val updatedTask = current.copy(
            subtasks = updatedSubtasks
        )

        updateTask(
            updatedTask = updatedTask,
            successMessage = if (checked) {
                "Đã hoàn thành việc nhỏ!"
            } else {
                "Đã bỏ đánh dấu việc nhỏ!"
            }
        )
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
                TextButton(
                    onClick = onBack,
                    enabled = !isUpdating
                ) {
                    Text("‹ Trang chủ")
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "Chi tiết",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.weight(1f))

                // Chức năng sửa sẽ làm sau.
                Text(
                    text = "Sửa",
                    color = TodoColors.Gray,
                    fontSize = 14.sp
                )
            }
        },

        bottomBar = {
            if (task != null && !loading && error == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DashboardBackground)
                        .navigationBarsPadding()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            markTaskCompleted()
                        },
                        enabled = !isUpdating &&
                                task?.done == false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                            Spacer(Modifier.width(10.dp))
                        }

                        Text(
                            text = when {
                                isUpdating ->
                                    "Đang cập nhật..."
                                task?.done == true ->
                                    "Đã hoàn thành"
                                else ->
                                    "Đánh dấu hoàn thành"
                            }
                        )
                    }

                    // DELETE API sẽ làm ở phần sau.
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { retry++ }
                    ) {
                        Text("Thử lại")
                    }
                }
            }

            task != null -> {
                val detail = task!!

                val completed = detail.subtasks.count {
                    it.done
                }

                val total = detail.subtasks.size

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Danh mục
                    Text(
                        text = if (
                            detail.category.equals(
                                "study",
                                ignoreCase = true
                            )
                        ) {
                            "Học tập"
                        } else {
                            "Cuộc sống"
                        },
                        fontSize = 12.sp,
                        color = TodoColors.Blue
                    )

                    Spacer(Modifier.height(8.dp))

                    // Tiêu đề công việc
                    Text(
                        text = detail.title,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 31.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    // Thông tin công việc
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
                            modifier = Modifier.padding(
                                vertical = 12.dp
                            )
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
                            modifier = Modifier.padding(
                                vertical = 12.dp
                            )
                        )

                        DetailInfoRow(
                            label = "Ưu tiên",
                            value = when (
                                detail.priority.lowercase()
                            ) {
                                "high" -> "Cao"
                                "low" -> "Thấp"
                                else -> "Vừa"
                            }
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Ghi chú
                    Text(
                        text = "Ghi chú",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = detail.note.ifBlank {
                            "Không có ghi chú"
                        },
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

                    // Tiến độ việc nhỏ
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Việc nhỏ",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "$completed/$total",
                            color = TodoColors.Gray
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (total == 0) {
                                0f
                            } else {
                                completed.toFloat() / total
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = TodoColors.Blue
                    )

                    Spacer(Modifier.height(14.dp))

                    // Danh sách việc nhỏ
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
                                text = "Chưa có việc nhỏ",
                                color = TodoColors.Gray
                            )
                        }

                        detail.subtasks.forEachIndexed {
                                index, subtask ->

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subtask.done,
                                    onCheckedChange = { checked ->
                                        toggleSubtask(
                                            index = index,
                                            checked = checked
                                        )
                                    },
                                    enabled = !isUpdating
                                )

                                Text(
                                    text = subtask.title,
                                    fontSize = 13.sp,
                                    textDecoration = if (
                                        subtask.done
                                    ) {
                                        TextDecoration.LineThrough
                                    } else {
                                        null
                                    },
                                    color = if (subtask.done) {
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

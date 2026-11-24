
package com.example.uthtodo.ui.screens

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.uthtodo.model.TaskDates
import com.example.uthtodo.model.TodoTask
import com.example.uthtodo.ui.components.TodoColors
import java.io.File

/**
 * Chương 12:
 * Tạo file TXT trong bộ nhớ cache của ứng dụng.
 * Chia sẻ file qua FileProvider và content:// URI.
 */
object TaskFileSharing {

    private fun buildText(tasks: List<TodoTask>): String {
        return buildString {
            appendLine("UTH TO DO - DANH SÁCH CÔNG VIỆC")
            appendLine("==============================")
            appendLine("Tổng số công việc: ${tasks.size}")
            appendLine(
                "Chưa hoàn thành: ${tasks.count { !it.isCompleted }}"
            )
            appendLine(
                "Đã hoàn thành: ${tasks.count { it.isCompleted }}"
            )
            appendLine()

            if (tasks.isEmpty()) {
                appendLine("Không có công việc để chia sẻ.")
            }

            tasks.forEachIndexed { index, task ->
                appendLine("${index + 1}. ${task.title}")
                appendLine("Nhóm: ${task.category.title}")
                appendLine(
                    "Ngày: ${TaskDates.date(task.dueAtMillis)}"
                )
                appendLine("Giờ: ${task.time}")
                appendLine("Ưu tiên: ${task.priority.title}")
                appendLine(
                    "Trạng thái: " +
                            if (task.isCompleted) {
                                "Đã hoàn thành"
                            } else {
                                "Chưa hoàn thành"
                            }
                )

                if (task.note.isNotBlank()) {
                    appendLine("Ghi chú: ${task.note}")
                }

                appendLine("------------------------------")
            }
        }
    }

    fun share(context: Context, tasks: List<TodoTask>) {
        // Thư mục được cho phép trong res/xml/file_paths.xml
        val sharedDir = File(context.cacheDir, "shared")

        if (!sharedDir.exists()) {
            check(sharedDir.mkdirs()) {
                "Không thể tạo thư mục chia sẻ."
            }
        }

        // Ghi nội dung vào tệp TXT trong bộ nhớ ứng dụng
        val file = File(sharedDir, "UTH_TODO.txt")

        file.writeText(
            text = buildText(tasks),
            charset = Charsets.UTF_8
        )

        // FileProvider tạo content URI an toàn
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Gửi tệp sang ứng dụng khác
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Danh sách công việc UTH-TODO"
            )
            putExtra(Intent.EXTRA_STREAM, fileUri)

            clipData = ClipData.newUri(
                context.contentResolver,
                "UTH_TODO.txt",
                fileUri
            )

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(
            sendIntent,
            "Chia sẻ danh sách công việc"
        )

        chooser.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        context.startActivity(chooser)
    }
}

/**
 * Nút chia sẻ dùng lại màu chủ đạo và Dark Mode.
 */
@Composable
fun ShareTaskFileButton(
    tasks: List<TodoTask>,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val primary = TodoColors.Blue

    OutlinedButton(
        onClick = {
            try {
                TaskFileSharing.share(
                    context = context,
                    tasks = tasks
                )
            } catch (e: Exception) {
                // Thông báo lỗi khi không chia sẻ được
                android.widget.Toast.makeText(
                    context,
                    "Không thể chia sẻ: " +
                            (e.localizedMessage ?: "Lỗi không xác định"),
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = primary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = primary
        )
    ) {
        Text(
            text = "Chia sẻ danh sách công việc",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


package com.example.uthtodo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// Dữ liệu công việc trả về từ API của thầy
data class TeachingTask(
    val id: Int,
    val title: String,
    val note: String,
    val category: String,
    val date: String,
    val time: String,
    val priority: String,
    val remind: Boolean,
    val done: Boolean,
    val subtasks: List<TeachingSubtask>
)

data class TeachingSubtask(
    val title: String,
    val done: Boolean
)

class TeachingTaskApi {

    companion object {
        private const val BASE_URL =
            "https://share.tryapps.store/mock/teach"

        // Token dùng chung do thầy cung cấp
        private const val MOCK_TOKEN =
            "TuanTruong-uth-student"
    }

    // GET /tasks - Lấy danh sách công việc
    suspend fun getTasks(): List<TeachingTask> =
        withContext(Dispatchers.IO) {

            val connection = (
                    URL("$BASE_URL/tasks").openConnection()
                            as HttpURLConnection
                    )

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $MOCK_TOKEN"
                )

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val statusCode = connection.responseCode

                if (statusCode !in 200..299) {
                    val message = connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        .orEmpty()

                    throw Exception(
                        "HTTP $statusCode: $message"
                    )
                }

                val json = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val array = JSONArray(json)

                (0 until array.length()).map { index ->
                    val item = array.getJSONObject(index)

                    val subtaskArray =
                        item.optJSONArray("subtasks")
                            ?: JSONArray()

                    val subtasks =
                        (0 until subtaskArray.length()).map {
                                subIndex ->

                            val sub =
                                subtaskArray.getJSONObject(
                                    subIndex
                                )

                            TeachingSubtask(
                                title = sub.optString("title"),
                                done = sub.optBoolean("done")
                            )
                        }

                    TeachingTask(
                        id = item.getInt("id"),
                        title = item.optString("title"),
                        note = item.optString("note"),
                        category = item.optString("category"),
                        date = item.optString("date"),
                        time = item.optString("time"),
                        priority = item.optString("priority"),
                        remind = item.optBoolean("remind"),
                        done = item.optBoolean("done"),
                        subtasks = subtasks
                    )
                }
            } finally {
                connection.disconnect()
            }
        }
}


package com.example.uthtodo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TeachingTaskPostApi {

    companion object {
        private const val BASE_URL =
            "https://share.tryapps.store/mock/teach"

        private const val MOCK_TOKEN =
            "TuanTruong-uth-student"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // POST /tasks - Thêm công việc mới
    suspend fun createTask(
        title: String,
        note: String,
        category: String,
        date: String,
        time: String,
        priority: String,
        remind: Boolean
    ): String = withContext(Dispatchers.IO) {

        require(title.isNotBlank()) {
            "Tên công việc không được để trống."
        }

        require(category in listOf("study", "life")) {
            "Danh mục không hợp lệ."
        }

        require(priority in listOf("low", "medium", "high")) {
            "Mức ưu tiên không hợp lệ."
        }

        val json = JSONObject().apply {
            put("title", title.trim())
            put("note", note)
            put("category", category)
            put("date", date)
            put("time", time)
            put("priority", priority)
            put("remind", remind)
            put("done", false)
            put("subtasks", JSONArray())
        }

        val requestBody = json.toString().toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )

        val request = Request.Builder()
            .url("$BASE_URL/tasks")
            .header(
                "Authorization",
                "Bearer $MOCK_TOKEN"
            )
            .header("Accept", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->

            val responseText =
                response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw Exception(
                    "POST thất bại: HTTP ${response.code}. " +
                            responseText
                )
            }

            // Trả nội dung phản hồi để bước sau
            // có thể xử lý ID công việc mới.
            responseText
        }
    }
}

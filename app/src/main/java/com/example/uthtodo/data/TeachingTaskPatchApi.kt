
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

class TeachingTaskPatchApi {

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

    // PATCH /tasks/{id}
    suspend fun updateTask(
        task: TeachingTask
    ): Unit = withContext(Dispatchers.IO) {

        val subtasks = JSONArray()

        task.subtasks.forEach { sub ->
            subtasks.put(
                JSONObject().apply {
                    put("title", sub.title)
                    put("done", sub.done)
                }
            )
        }

        val json = JSONObject().apply {
            put("title", task.title)
            put("note", task.note)
            put("category", task.category)
            put("date", task.date)
            put("time", task.time)
            put("priority", task.priority)
            put("remind", task.remind)
            put("done", task.done)
            put("subtasks", subtasks)
        }

        val request = Request.Builder()
            .url("$BASE_URL/tasks/${task.id}")
            .header(
                "Authorization",
                "Bearer $MOCK_TOKEN"
            )
            .header("Accept", "application/json")
            .patch(
                json.toString().toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception(
                    "PATCH thất bại: HTTP ${response.code} - " +
                            response.body?.string().orEmpty()
                )
            }
        }
    }
}

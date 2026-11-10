
package com.example.uthtodo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class TeachingTaskDeleteApi {

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

    // DELETE /tasks/{id}
    suspend fun deleteTask(id: Int): Unit =
        withContext(Dispatchers.IO) {

            val request = Request.Builder()
                .url("$BASE_URL/tasks/$id")
                .header(
                    "Authorization",
                    "Bearer $MOCK_TOKEN"
                )
                .header("Accept", "application/json")
                .delete()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody =
                        response.body?.string().orEmpty()

                    throw Exception(
                        "DELETE thất bại: HTTP ${response.code}" +
                                " $errorBody"
                    )
                }
            }
        }
}

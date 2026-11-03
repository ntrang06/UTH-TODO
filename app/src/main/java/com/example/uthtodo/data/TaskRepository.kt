package com.example.uthtodo.data

import com.example.uthtodo.model.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()

    private fun collection(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("tasks")

    fun observe(
        uid: String,
        onChange: (TaskFeed) -> Unit
    ): ListenerRegistration {
        require(uid.isNotBlank())

        return collection(uid).addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            if (error != null) {
                onChange(
                    TaskFeed(
                        isLoading = false,
                        error = errorMessage(error)
                    )
                )
            } else if (snapshot != null) {
                onChange(
                    TaskFeed(
                        tasks = snapshot.documents
                            .mapNotNull { readTask(it) }
                            .sortedWith(
                                compareBy<TodoTask> { it.dueAtMillis }
                                    .thenBy { it.id }
                            ),
                        isLoading = false,
                        hasPendingWrites = snapshot.metadata.hasPendingWrites(),
                        fromCache = snapshot.metadata.isFromCache
                    )
                )
            }
        }
    }

    fun add(
        uid: String,
        task: TodoTask,
        onError: (String) -> Unit
    ) {
        collection(uid).document(task.id).set(
            mapOf(
                "title" to task.title,
                "category" to task.category.name,
                "time" to task.time,
                "isCompleted" to task.isCompleted,
                "note" to task.note,
                "dueAtMillis" to task.dueAtMillis,
                "priority" to task.priority.name,
                "completedAtMillis" to task.completedAtMillis
            )
        ).addOnFailureListener {
            onError(errorMessage(it))
        }
    }

    fun setCompleted(
        uid: String,
        task: TodoTask,
        completed: Boolean,
        completedAt: Long?,
        onError: (String) -> Unit
    ) {
        collection(uid).document(task.id).update(
            mapOf(
                "isCompleted" to completed,
                "completedAtMillis" to completedAt
            )
        ).addOnFailureListener {
            onError(errorMessage(it))
        }
    }

    private fun readTask(doc: DocumentSnapshot): TodoTask? {
        val title = doc.getString("title") ?: return null

        val category = TodoCategory.entries.firstOrNull {
            it.name == doc.getString("category")
        } ?: return null

        val due = doc.getLong("dueAtMillis") ?: return null

        return TodoTask(
            id = doc.id,
            title = title,
            category = category,
            time = doc.getString("time") ?: TaskDates.time(due),
            isCompleted = doc.getBoolean("isCompleted") ?: false,
            note = doc.getString("note").orEmpty(),
            dueAtMillis = due,
            priority = TaskPriority.entries.firstOrNull {
                it.name == doc.getString("priority")
            } ?: TaskPriority.MEDIUM,
            completedAtMillis = doc.getLong("completedAtMillis")
        )
    }

    private fun errorMessage(error: Exception): String {
        return when (
            (error as? FirebaseFirestoreException)?.code
        ) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "Không có quyền truy cập. Hãy kiểm tra Firestore Rules và tài khoản đăng nhập."

            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Chưa kết nối được Firestore. Hãy kiểm tra Internet."

            else ->
                error.localizedMessage
                    ?: "Không truy cập được dữ liệu công việc."
        }
    }
}
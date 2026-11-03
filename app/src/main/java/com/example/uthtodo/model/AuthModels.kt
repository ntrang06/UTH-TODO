package com.example.uthtodo.model

// Đối tượng chứa thông tin người dùng.
data class AppUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?
) {
    // Ví dụ: Minh Anh → MA.
    fun initials(): String {
        return displayName
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .takeLast(2)
            .joinToString("") {
                it.take(1).uppercase()
            }
            .ifBlank { "UT" }
    }
}

// Các kết quả có thể nhận được khi đăng nhập.
sealed class SignInResult {

    data class Success(
        val user: AppUser
    ) : SignInResult()

    data class Error(
        val message: String
    ) : SignInResult()

    object Cancelled : SignInResult()
}
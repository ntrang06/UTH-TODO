package com.example.uthtodo.model

data class LoginContent(
    val title: String = "Chào mừng đến với\nUTH Todo",
    val description: String =
        "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",

    val benefits: List<String> = listOf(
        "Tự động sao lưu, không lo mất dữ liệu",
        "Dùng trên iPhone, iPad và Mac"
    ),

    val googleLabel: String = "Tiếp tục với Google",
    val loadingLabel: String = "Đang đăng nhập…",
    val guestLabel: String = "Dùng thử không cần tài khoản",
    val separatorLabel: String = "hoặc",

    val legalText: String =
        "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",

    val backLabel: String = "Quay lại",
    val errorTitle: String = "Đăng nhập chưa thành công",
    val dismissLabel: String = "Đóng",
    val successMessage: String = "Đăng nhập thành công",
    val cancelledMessage: String = "Bạn đã hủy đăng nhập.",
    val activityError: String =
        "Không mở được cửa sổ đăng nhập. Hãy mở lại ứng dụng."
)

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
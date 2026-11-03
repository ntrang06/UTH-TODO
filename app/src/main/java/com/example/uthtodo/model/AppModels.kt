package com.example.uthtodo.model

// Thông tin ứng dụng.
data class AppInfo(
    val name: String,
    val slogan: String,
    val version: String,
    val skipLabel: String,
    val logoDescription: String,
    val splashDurationMillis: Long
)

// Ba loại hình minh họa.
enum class IllustrationType {
    TASKS,
    CATEGORIES,
    REMINDER
}

// Lớp cha: quy định thông tin chung của một trang giới thiệu.
// Thể hiện tính TRỪU TƯỢNG.
sealed class OnboardingPage(
    val title: String,
    val description: String
) {
    abstract val illustrationType: IllustrationType

    abstract fun buttonText(): String
}

// Trang giới thiệu 1.
// KẾ THỪA từ OnboardingPage.
class OverviewPage(
    title: String,
    description: String,
    private val nextText: String
) : OnboardingPage(title, description) {

    override val illustrationType = IllustrationType.TASKS

    override fun buttonText(): String = nextText
}

// Trang giới thiệu 2.
class CategoriesPage(
    title: String,
    description: String,
    private val nextText: String
) : OnboardingPage(title, description) {

    override val illustrationType = IllustrationType.CATEGORIES

    override fun buttonText(): String = nextText
}

// Trang giới thiệu 3.
class ReminderPage(
    title: String,
    description: String,
    private val startText: String
) : OnboardingPage(title, description) {

    override val illustrationType = IllustrationType.REMINDER

    override fun buttonText(): String = startText
}

// Dữ liệu công việc minh họa.
data class TaskPreview(
    val title: String,
    val isDone: Boolean
)

// Dữ liệu nhóm công việc.
data class CategoryPreview(
    val name: String,
    val taskName: String
)

// Dữ liệu lời nhắc minh họa.
data class ReminderPreview(
    val message: String,
    val iconDescription: String
)

// Gom dữ liệu dùng cho các hình minh họa.
data class IllustrationData(
    val tasks: List<TaskPreview>,
    val categories: List<CategoryPreview>,
    val reminder: ReminderPreview
)

// Dữ liệu màn hình Trang chủ.
data class HomeContent(
    val title: String,
    val message: String,
    val replayLabel: String
)
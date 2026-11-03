package com.example.uthtodo.data

import com.example.uthtodo.model.*

class TodoRepository {

    // ĐÓNG GÓI:
    // Danh sách gốc là private, bên ngoài không truy cập trực tiếp.
    private val pages: List<OnboardingPage> = listOf(
        OverviewPage(
            title = "Mọi việc, gọn trong một\nchỗ",
            description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
            nextText = "Tiếp tục"
        ),

        CategoriesPage(
            title = "Chia theo Học tập và\nCuộc sống",
            description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
            nextText = "Tiếp tục"
        ),

        ReminderPage(
            title = "Nhắc đúng lúc",
            description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
            startText = "Bắt đầu ngay"
        )
    )

    // Cung cấp bản sao danh sách cho giao diện.
    fun getPages(): List<OnboardingPage> {
        return pages.toList()
    }

    // Tạo đối tượng chứa thông tin ứng dụng.
    fun getAppInfo(): AppInfo {
        return AppInfo(
            name = "UTH Todo",
            slogan = "Việc học, việc nhà – gọn gàng mỗi ngày",
            version = "Phiên bản 2.1.0",
            skipLabel = "Bỏ qua",
            logoDescription = "Biểu tượng danh sách công việc",
            splashDurationMillis = 1500L
        )
    }

    // Tạo các đối tượng dữ liệu cho hình minh họa.
    fun getIllustrationData(): IllustrationData {
        return IllustrationData(
            tasks = listOf(
                TaskPreview(
                    title = "Hoàn thành bài tập",
                    isDone = true
                ),
                TaskPreview(
                    title = "Dọn dẹp phòng",
                    isDone = false
                ),
                TaskPreview(
                    title = "Chuẩn bị bài mới",
                    isDone = false
                )
            ),

            categories = listOf(
                CategoryPreview(
                    name = "Học tập",
                    taskName = "Làm bài tập Navigation"
                ),
                CategoryPreview(
                    name = "Cuộc sống",
                    taskName = "Dọn dẹp góc học tập"
                )
            ),

            reminder = ReminderPreview(
                message = "Ôn bài Toán lúc 19:00",
                iconDescription = "Chuông nhắc việc"
            )
        )
    }

    // Tạo đối tượng chứa nội dung Trang chủ.
    fun getHomeContent(): HomeContent {
        return HomeContent(
            title = "Chào mừng đến với UTH Todo!",
            message = "Bạn đã hoàn thành phần giới thiệu.",
            replayLabel = "Xem lại giới thiệu"
        )
    }
}
package com.example.studysync.data
data class ClassSession(
    val id: String,
    val subject: String,
    val teacher: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: DayOfWeek,
    val colorIndex: Int = 0
)

enum class DayOfWeek(val displayVi: String, val short: String) {
    MONDAY("Thứ 2", "T2"), TUESDAY("Thứ 3", "T3"), WEDNESDAY("Thứ 4", "T4"),
    THURSDAY("Thứ 5", "T5"), FRIDAY("Thứ 6", "T6"), SATURDAY("Thứ 7", "T7"), SUNDAY("Chủ nhật", "CN")
}

data class ExamReminder(
    val id: String,
    val subject: String,
    val examType: ExamType,
    val date: String,
    val time: String,
    val room: String,
    val notes: String = "",
    val daysUntil: Int,
    val colorIndex: Int = 0
)

enum class ExamType(val label: String) {
    MIDTERM("Giữa kỳ"), FINAL("Cuối kỳ"), QUIZ("Kiểm tra"), LAB("Thực hành")
}

object SampleData {
    val classSessions = listOf(
        ClassSession("1", "Lập trình cơ bản", "TS. Nguyễn Văn A", "A1-301", "07:30", "09:10", DayOfWeek.MONDAY, 0),
        ClassSession("2", "Vật lý đại cương", "ThS. Trần Thị B", "B2-202", "09:20", "11:00", DayOfWeek.MONDAY, 1),
        ClassSession("3", "Lập trình Android", "TS. Lê Văn C", "Lab 401", "13:00", "15:30", DayOfWeek.MONDAY, 2),
        ClassSession("4", "Cấu trúc dữ liệu", "ThS. Phạm Thị D", "A3-105", "07:30", "09:10", DayOfWeek.TUESDAY, 3),
        ClassSession("5", "Mạng máy tính", "TS. Hoàng Văn E", "A2-204", "09:20", "11:00", DayOfWeek.TUESDAY, 4)
    )
    val examReminders = listOf(
        ExamReminder("e1", "Lập trình cơ bản", ExamType.MIDTERM, "28/05/2026", "07:30", "A1-301", "Chương 1–4", daysUntil = 3, colorIndex = 0),
        ExamReminder("e2", "Vật lý đại cương", ExamType.LAB, "30/05/2026", "13:00", "Lab 201", "Mang đồ bảo hộ", daysUntil = 5, colorIndex = 1),
        ExamReminder("e3", "Cấu trúc dữ liệu", ExamType.QUIZ, "02/06/2026", "09:20", "A3-105", "Trees & Graphs", daysUntil = 8, colorIndex = 3)
    )
    val todaySessions = classSessions.filter { it.dayOfWeek == DayOfWeek.MONDAY }
    val upcomingExams = examReminders.sortedBy { it.daysUntil }.take(3)
}
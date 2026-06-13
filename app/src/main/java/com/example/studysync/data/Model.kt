package com.example.studysync.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_sessions")
data class ClassSession(
    @PrimaryKey val id: String,
    val subject: String,
    val teacher: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: DayOfWeek,
    val colorIndex: Int = 0,
)

enum class DayOfWeek(val displayVi: String, val short: String) {
    MONDAY("Thứ 2", "T2"), TUESDAY("Thứ 3", "T3"), WEDNESDAY("Thứ 4", "T4"),
    THURSDAY("Thứ 5", "T5"), FRIDAY("Thứ 6", "T6"), SATURDAY("Thứ 7", "T7"), SUNDAY("Chủ nhật", "CN")
}

@Entity(tableName = "exam_reminders")
data class ExamReminder(
    @PrimaryKey val id: String,
    val subject: String,
    val examType: ExamType,
    val date: String,
    val time: String,
    val room: String,
    val notes: String = "",
    val daysUntil: Int,
    val colorIndex: Int = 0,
)

enum class ExamType(val label: String) {
    MIDTERM("Giữa kỳ"), FINAL("Cuối kỳ"), QUIZ("Kiểm tra"), LAB("Thực hành")
}

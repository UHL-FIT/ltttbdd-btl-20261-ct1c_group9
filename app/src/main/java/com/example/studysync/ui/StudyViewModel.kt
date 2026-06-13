package com.example.studysync.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import com.example.studysync.data.local.StudyDatabase
import com.example.studysync.receiver.ExamReminderReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = StudyDatabase.getDatabase(application).studyDao()
    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val allSessions: StateFlow<List<ClassSession>> = dao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<ExamReminder>> = dao.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Đã xóa phần nạp dữ liệu mẫu (init block) để app hoàn toàn trống ban đầu

    fun addSession(session: ClassSession) {
        viewModelScope.launch {
            dao.insertSession(session)
        }
    }

    fun addExam(exam: ExamReminder) {
        viewModelScope.launch {
            dao.insertExam(exam)
            scheduleExamReminder(exam)
        }
    }

    private fun scheduleExamReminder(exam: ExamReminder) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val examDateTime = sdf.parse("${exam.date} ${exam.time}")
            
            if (examDateTime != null && examDateTime.after(Date())) {
                val intent = Intent(getApplication(), ExamReminderReceiver::class.java).apply {
                    putExtra("subject", exam.subject)
                    putExtra("type", exam.examType.label)
                    putExtra("time", exam.time)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    getApplication(),
                    exam.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Hẹn giờ thông báo (Ví dụ: Đúng giờ thi)
                // Bạn có thể trừ đi 30 phút nếu muốn nhắc trước: examDateTime.time - (30 * 60 * 1000)
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    examDateTime.time,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteSession(session: ClassSession) {
        viewModelScope.launch {
            dao.deleteSession(session)
        }
    }

    fun deleteExam(exam: ExamReminder) {
        viewModelScope.launch {
            cancelExamReminder(exam)
            dao.deleteExam(exam)
        }
    }

    private fun cancelExamReminder(exam: ExamReminder) {
        val intent = Intent(getApplication(), ExamReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            exam.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}

package com.example.studysync.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studysync.StudySyncApplication
import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import com.example.studysync.data.repository.StudyRepository
import com.example.studysync.receiver.ExamReminderReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudyViewModel(
    private val application: Application,
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val allSessions: StateFlow<List<ClassSession>> = studyRepository.getAllSessionsStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<ExamReminder>> = studyRepository.getAllExamsStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSession(session: ClassSession) {
        viewModelScope.launch {
            studyRepository.insertSession(session)
        }
    }

    fun addExam(exam: ExamReminder) {
        viewModelScope.launch {
            studyRepository.insertExam(exam)
            scheduleExamReminder(exam)
        }
    }

    private fun scheduleExamReminder(exam: ExamReminder) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val examDateTime = sdf.parse("${exam.date} ${exam.time}")
            
            if (examDateTime != null && examDateTime.after(Date())) {
                val intent = Intent(application, ExamReminderReceiver::class.java).apply {
                    putExtra("subject", exam.subject)
                    putExtra("type", exam.examType.label)
                    putExtra("time", exam.time)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    application,
                    exam.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

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
            studyRepository.deleteSession(session)
        }
    }

    fun deleteExam(exam: ExamReminder) {
        viewModelScope.launch {
            cancelExamReminder(exam)
            studyRepository.deleteExam(exam)
        }
    }

    private fun cancelExamReminder(exam: ExamReminder) {
        val intent = Intent(application, ExamReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            exam.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StudySyncApplication)
                StudyViewModel(application, application.container.studyRepository)
            }
        }
    }
}

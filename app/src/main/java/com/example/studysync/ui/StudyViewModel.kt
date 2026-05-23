package com.example.studysync.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import com.example.studysync.data.local.StudyDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = StudyDatabase.getDatabase(application).studyDao()

    val allSessions: StateFlow<List<ClassSession>> = dao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<ExamReminder>> = dao.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (dao.getAllSessions().first().isEmpty()) {
                com.example.studysync.data.SampleData.classSessions.forEach { dao.insertSession(it) }
            }
            if (dao.getAllExams().first().isEmpty()) {
                com.example.studysync.data.SampleData.examReminders.forEach { dao.insertExam(it) }
            }
        }
    }

    fun addSession(session: ClassSession) {
        viewModelScope.launch {
            dao.insertSession(session)
        }
    }

    fun addExam(exam: ExamReminder) {
        viewModelScope.launch {
            dao.insertExam(exam)
        }
    }

    fun deleteSession(session: ClassSession) {
        viewModelScope.launch {
            dao.deleteSession(session)
        }
    }

    fun deleteExam(exam: ExamReminder) {
        viewModelScope.launch {
            dao.deleteExam(exam)
        }
    }
}

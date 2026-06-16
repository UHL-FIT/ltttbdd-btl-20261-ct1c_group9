package com.example.studysync.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studysync.data.ExamReminder
import com.example.studysync.ui.StudyViewModel
import com.example.studysync.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Exams : Screen("exams")
    object Manage : Screen("manage")
    object About : Screen("about")
    object Stats : Screen("stats")
}

// Hàm tiện ích tính số ngày thực tế (Dùng chung cho cả app)
fun getLiveDaysUntil(dateStr: String): Int {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        val examDate = sdf.parse(dateStr)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.time
        
        if (examDate != null) {
            val diff = examDate.time - today.time
            (diff / (24 * 60 * 60 * 1000)).toInt()
        } else 0
    } catch (e: Exception) { 0 }
}

@Composable
fun StudySyncNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {
    val viewModel: StudyViewModel = viewModel(factory = StudyViewModel.Factory)
    val sessions by viewModel.allSessions.collectAsState()
    val exams by viewModel.allExams.collectAsState()
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val calendar = Calendar.getInstance()
            val currentDayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "MONDAY"; Calendar.TUESDAY -> "TUESDAY"
                Calendar.WEDNESDAY -> "WEDNESDAY"; Calendar.THURSDAY -> "THURSDAY"
                Calendar.FRIDAY -> "FRIDAY"; Calendar.SATURDAY -> "SATURDAY"
                Calendar.SUNDAY -> "SUNDAY"; else -> "MONDAY"
            }
            
            val todaySessions = sessions.filter { it.dayOfWeek.name == currentDayName }
            
            // Cập nhật lại số ngày "sống" và lọc bỏ kỳ thi đã qua
            val upcomingExams = exams.map { 
                it.copy(daysUntil = getLiveDaysUntil(it.date)) 
            }.filter { it.daysUntil >= 0 } // Chỉ hiện kỳ thi hôm nay hoặc tương lai
             .sortedBy { it.daysUntil }
             .take(3)

            HomeScreen(
                sessions = todaySessions,
                exams = upcomingExams,
                isDarkMode = isDarkMode,
                onThemeToggle = { targetDarkMode -> mainViewModel.toggleTheme(targetDarkMode) },
                onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                onNavigateToExams = { navController.navigate(Screen.Exams.route) },
                onNavigateToManage = { navController.navigate(Screen.Manage.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
            )
        }

        composable(Screen.Schedule.route) {
            TimetableScreen(
                sessions = sessions,
                onAddSession = { viewModel.addSession(it) },
                onDeleteSession = { id -> 
                    sessions.find { it.id == id }?.let { viewModel.deleteSession(it) }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Exams.route) {
            // Cập nhật số ngày sống cho màn hình danh sách thi
            val liveExams = exams.map { it.copy(daysUntil = getLiveDaysUntil(it.date)) }
            ExamScreen(navController = navController, viewModel = viewModel, liveExams = liveExams)
        }

        composable(Screen.Manage.route) {
            ManageScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Stats.route) {
            StatisticScreen(
                sessions = sessions,
                exams = exams.map { it.copy(daysUntil = getLiveDaysUntil(it.date)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

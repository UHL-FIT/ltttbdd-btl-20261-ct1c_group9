package com.example.studysync.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studysync.data.SampleData

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Exams : Screen("exams")
}

@Composable
fun StudySyncNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                sessions = SampleData.todaySessions, // Đổ dữ liệu mẫu vào trang chủ
                exams = SampleData.upcomingExams,     // Đổ dữ liệu mẫu vào trang chủ
                onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                onNavigateToExams = { navController.navigate(Screen.Exams.route) }
            )
        }

        composable(Screen.Schedule.route) {
            // Mai bảo Coder cắt file ScheduleScreen gốc dán vào đây, tạm thời để trống
        }

        composable(Screen.Exams.route) {
            // Mai bảo Coder cắt file ExamScreen gốc dán vào đây, tạm thời để trống
        }
    }
}
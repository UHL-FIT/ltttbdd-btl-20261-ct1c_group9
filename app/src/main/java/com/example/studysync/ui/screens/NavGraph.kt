package com.example.studysync.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studysync.data.*

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Schedule : Screen("schedule")
    object Exams    : Screen("exams")
    object Stats    : Screen("stats")
}

@Composable
fun StudySyncNavGraph(navController: NavHostController) {
    // Shared mutable state across screens
    var sessions by remember { mutableStateOf(SampleData.classSessions) }
    var exams    by remember { mutableStateOf(SampleData.examReminders) }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                sessions = sessions.filter { it.dayOfWeek == DayOfWeek.MONDAY },
                exams    = exams.sortedBy { it.daysUntil }.take(3),
                onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                onNavigateToExams    = { navController.navigate(Screen.Exams.route) }
            )
        }
        composable(Screen.Schedule.route) {
            TimetableScreen(
                sessions   = sessions,
                onAddSession    = { sessions = sessions + it },
                onDeleteSession = { id -> sessions = sessions.filter { it.id != id } },
                onBack     = { navController.popBackStack() }
            )
        }
        composable(Screen.Exams.route) {
            ExamScreen(
                exams     = exams,
                onAddExam    = { exams = exams + it },
                onDeleteExam = { id -> exams = exams.filter { it.id != id } },
                onViewStats  = { navController.navigate(Screen.Stats.route) },
                onBack    = { navController.popBackStack() }
            )
        }
        composable(Screen.Stats.route) {
            StatisticScreen(
                sessions = sessions,
                exams    = exams,
                onBack   = { navController.popBackStack() }
            )
        }
    }
}

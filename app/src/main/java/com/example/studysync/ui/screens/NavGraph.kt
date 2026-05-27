package com.example.studysync.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studysync.ui.StudyViewModel
import java.util.Calendar

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Exams : Screen("exams")
    object Manage : Screen("manage")
    object About : Screen("about")
}

@Composable
fun StudySyncNavGraph(navController: NavHostController) {
    val viewModel: StudyViewModel = viewModel()
    val sessions by viewModel.allSessions.collectAsState()
    val exams by viewModel.allExams.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val currentDay = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "MONDAY"
                Calendar.TUESDAY -> "TUESDAY"
                Calendar.WEDNESDAY -> "WEDNESDAY"
                Calendar.THURSDAY -> "THURSDAY"
                Calendar.FRIDAY -> "FRIDAY"
                Calendar.SATURDAY -> "SATURDAY"
                Calendar.SUNDAY -> "SUNDAY"
                else -> "MONDAY"
            }
            
            val todaySessions = sessions.filter { it.dayOfWeek.name == currentDay }
            val upcomingExams = exams.take(3)

            HomeScreen(
                sessions = todaySessions,
                exams = upcomingExams,
                onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                onNavigateToExams = { navController.navigate(Screen.Exams.route) },
                onNavigateToManage = { navController.navigate(Screen.Manage.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Schedule.route) {
            TimetableScreen(navController = navController)
        }

        composable(Screen.Exams.route) {
            ExamScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Manage.route) {
            ManageScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}

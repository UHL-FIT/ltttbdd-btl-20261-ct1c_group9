package com.example.studysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.studysync.ui.theme.StudySyncTheme
import com.example.studysync.ui.screens.StudySyncNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySyncTheme {
                val navController = rememberNavController()
                StudySyncNavGraph(navController = navController)
            }
        }
    }
}
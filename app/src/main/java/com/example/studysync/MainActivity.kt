package com.example.studysync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.studysync.ui.theme.StudySyncTheme
import com.example.studysync.ui.screens.StudySyncNavGraph
import com.example.studysync.ui.viewmodel.MainViewModel
import com.example.studysync.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val TAG = "StudySyncLifecycle"

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Bước 1: onCreate - Khởi tạo ứng dụng")

        NotificationHelper(this).createNotificationChannel()
        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            StudySyncTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                StudySyncNavGraph(navController = navController)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Bước 2: onPause - Ứng dụng bị tạm dừng tương tác")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Bước 3: onStop - Ứng dụng đã bị ẩn hoàn toàn")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Bước 4: onDestroy - Ứng dụng bị hủy hoàn toàn")
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
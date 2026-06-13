package com.example.studysync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.studysync.ui.theme.StudySyncTheme
import com.example.studysync.ui.screens.StudySyncNavGraph
import com.example.studysync.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Xử lý sau khi người dùng chọn Cho phép/Từ chối nếu cần
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Khởi tạo Notification Channel
        NotificationHelper(this).createNotificationChannel()
        
        // Xin quyền thông báo cho Android 13+
        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            StudySyncTheme {
                val navController = rememberNavController()
                StudySyncNavGraph(navController = navController)
            }
        }
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
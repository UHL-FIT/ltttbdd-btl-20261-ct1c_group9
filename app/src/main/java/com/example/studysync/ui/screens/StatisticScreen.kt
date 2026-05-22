package com.example.studysync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Thống Kê Học Tập") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: CODER CHÍNH PHỤ TRÁCH GIAO DIỆN VÀ LOGIC
            // - Khu vực hiển thị Đếm ngược ngày thi (Tính toán Min/Max thời gian bằng Kotlin)
            // - Khu vực hiển thị Tổng số tín chỉ tích lũy trong kỳ
            // - Đặt 2 nút bấm: "Nhập dữ liệu (Import JSON)" và "Xuất dữ liệu (Export JSON)"

            Text(text = "Khung xương màn hình Thống Kê")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại Trang Chủ")
            }
        }
    }
}
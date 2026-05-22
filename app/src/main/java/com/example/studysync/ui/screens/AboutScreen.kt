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
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Thông Tin Ứng Dụng") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: CODER PHỤ (BẠN MỚI) PHỤ TRÁCH LÀM MÀN NÀY
            // - Dùng Column căn giữa đẹp đẽ hiển thị: Tên App, Phiên bản (v1.0)
            // - Hiển thị danh sách thành viên nhóm: Họ tên, MSV, Vai trò (Tech Lead, Coder, Tester...)

            Text(text = "Khung xương màn hình Giới Thiệu Nhóm")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại Trang Chủ")
            }
        }
    }
}
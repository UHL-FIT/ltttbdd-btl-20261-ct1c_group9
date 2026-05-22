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
fun ManageScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản Lý Nhập Liệu (CRUD)") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: CODER PHỤ (BẠN MỚI) PHỤ TRÁCH LÀM FORM NHẬP Ở ĐÂY
            // - Thiết kế 2 Form riêng biệt (hoặc dùng Tab): Thêm Môn Học & Thêm Lịch Thi
            // - Sử dụng OutlinedTextField cho các trường dữ liệu (Tên, Phòng, Ngày, Số tín chỉ...)
            // - Thêm nút "Lưu dữ liệu" (Hiện tại bấm vào chỉ cần hiện Toast/SnackBar thông báo)

            Text(text = "Khung xương màn hình Quản Lý CRUD")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại Trang Chủ")
            }
        }
    }
}
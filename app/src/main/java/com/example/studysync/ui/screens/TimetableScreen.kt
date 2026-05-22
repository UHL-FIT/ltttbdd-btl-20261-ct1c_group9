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
fun TimetableScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Lịch Học Theo Tuần") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: CODER CHÍNH LÀM GIAO DIỆN Ở ĐÂY
            // - Dùng LazyColumn hiển thị danh sách các Thứ trong tuần (Thứ 2 -> Thứ 7)
            // - Mỗi môn học thiết kế dạng Card (Tên môn, Phòng học, Ca học, Số tín chỉ)
            // - Thêm nút hoặc Icon để bấm sửa/xóa nhanh môn học

            Text(text = "Khung xương màn hình Lịch Học")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại Trang Chủ")
            }
        }
    }
}
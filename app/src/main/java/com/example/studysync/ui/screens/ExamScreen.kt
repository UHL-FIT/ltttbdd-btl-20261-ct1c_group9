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
fun ExamScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Danh Sách Lịch Thi") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: CODER CHÍNH LÀM GIAO DIỆN Ở ĐÂY
            // - Dùng LazyColumn hiển thị danh sách lịch thi xếp theo ngày gần nhất
            // - Mỗi item hiển thị: Tên môn thi, Ngày thi, Giờ thi, Phòng thi, Hình thức thi

            Text(text = "Khung xương màn hình Lịch Thi")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại Trang Chủ")
            }
        }
    }
}
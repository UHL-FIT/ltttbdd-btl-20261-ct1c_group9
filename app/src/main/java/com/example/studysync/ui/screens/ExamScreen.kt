package com.example.studysync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

// 1. Data Model
data class ExamSchedule(
    val subjectName: String,
    val date: String,
    val time: String,
    val room: String,
    val examFormat: String,
    val lecturer: String = "Chưa cập nhật",
    val notes: String = "Không có ghi chú"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(navController: NavController) {
    // Dữ liệu mẫu (Sau này bạn sẽ lấy từ ViewModel hoặc Database)
    val examList = listOf(
        ExamSchedule("Lập trình Android", "20/12/2023", "07:30", "P.402", "Trắc nghiệm", "TS. Nguyễn Văn A", "Mang theo laptop và sạc"),
        ExamSchedule("Cấu trúc dữ liệu", "22/12/2023", "13:30", "P.101", "Tự luận", "ThS. Trần Thị B", "Được sử dụng 1 tờ giấy A4 tài liệu"),
        ExamSchedule("Tiếng Anh chuyên ngành", "25/12/2023", "09:00", "P.Online", "Trực tuyến", "Ms. Lê Thị C", "Kiểm tra webcam trước khi thi"),
        ExamSchedule("Toán rời rạc", "28/12/2023", "07:30", "P.505", "Tự luận", "TS. Hoàng Văn D", "Tập trung trước 15 phút")
    )

    var selectedExam by remember { mutableStateOf<ExamSchedule?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh Sách Lịch Thi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(examList) { exam ->
                    ExamItemCard(exam = exam, onClick = { selectedExam = exam })
                }
            }

            // Hiển thị Dialog chi tiết khi có môn được chọn
            selectedExam?.let { exam ->
                ExamDetailDialog(exam = exam, onDismiss = { selectedExam = null })
            }
        }
    }
}

@Composable
fun ExamItemCard(exam: ExamSchedule, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exam.subjectName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "📅 Ngày: ${exam.date}", fontSize = 14.sp)
                Text(text = "⏰ Giờ: ${exam.time}", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "📍 Phòng: ${exam.room}", fontSize = 14.sp)
                Text(
                    text = exam.examFormat,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE91E63)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Xem chi tiết...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun ExamDetailDialog(exam: ExamSchedule, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = exam.subjectName,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(label = "📅 Ngày thi:", value = exam.date)
                DetailRow(label = "⏰ Giờ thi:", value = exam.time)
                DetailRow(label = "📍 Phòng thi:", value = exam.room)
                DetailRow(label = "📝 Hình thức:", value = exam.examFormat)
                DetailRow(label = "👨‍🏫 Giảng viên:", value = exam.lecturer)
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Ghi chú:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = exam.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.width(110.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

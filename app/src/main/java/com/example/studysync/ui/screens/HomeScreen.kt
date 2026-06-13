package com.example.studysync.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.studysync.data.*
import com.example.studysync.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessions: List<ClassSession>,
    exams: List<ExamReminder>,
    onNavigateToSchedule: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToManage: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSession by remember { mutableStateOf<ClassSession?>(null) }
    var selectedExam by remember { mutableStateOf<ExamReminder?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val dayOfWeekVi = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Thứ 2"
        Calendar.TUESDAY -> "Thứ 3"
        Calendar.WEDNESDAY -> "Thứ 4"
        Calendar.THURSDAY -> "Thứ 5"
        Calendar.FRIDAY -> "Thứ 6"
        Calendar.SATURDAY -> "Thứ 7"
        Calendar.SUNDAY -> "Chủ nhật"
        else -> "Hôm nay"
    }

    // Tính toán thống kê thật
    val uniqueSubjectsCount = sessions.map { it.subject }.distinct().size
    val totalHours = sessions.sumOf { session ->
        try {
            val start = session.startTime.split(":")
            val end = session.endTime.split(":")
            val diffMinutes = (end[0].toInt() * 60 + end[1].toInt()) - (start[0].toInt() * 60 + start[1].toInt())
            diffMinutes / 60.0
        } catch (e: Exception) { 0.0 }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { HomeHeader() }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    QuickActionsRow(
                        onScheduleClick = onNavigateToSchedule,
                        onExamsClick = onNavigateToExams,
                        onManageClick = onNavigateToManage,
                        onAboutClick = onNavigateToAbout
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionTitle(title = "Lịch hôm nay", subtitle = "$dayOfWeekVi - Dự kiến", icon = Icons.Default.EventNote)
                    Spacer(Modifier.height(12.dp))
                    if (sessions.isEmpty()) {
                        EmptyStateCard(message = "Hôm nay bạn được nghỉ học!")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            sessions.forEach { session ->
                                TodayClassCard(session = session, onClick = {
                                    selectedSession = session
                                    selectedExam = null
                                    showDialog = true
                                })
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionTitle(title = "Kỳ thi sắp tới", subtitle = "Cần chuẩn bị ngay", icon = Icons.AutoMirrored.Filled.Assignment)
                    Spacer(Modifier.height(12.dp))
                    if (exams.isEmpty()) {
                        EmptyStateCard(message = "Không có kỳ thi nào sắp tới.")
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            items(exams) { exam ->
                                UpcomingExamCard(exam = exam, onClick = {
                                    selectedExam = exam
                                    selectedSession = null
                                    showDialog = true
                                })
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    StudyStatsSection(
                        totalSessions = sessions.size,
                        totalExams = exams.size,
                        subjectsCount = uniqueSubjectsCount,
                        hoursCount = totalHours,
                        onClick = onNavigateToStats
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) { MotivationCard() }
            }
            
            item { Spacer(Modifier.height(16.dp)) }
        }

        if (showDialog) {
            DetailDialog(session = selectedSession, exam = selectedExam, onDismiss = { showDialog = false })
        }
    }
}

@Composable
private fun StudyStatsSection(
    totalSessions: Int, 
    totalExams: Int, 
    subjectsCount: Int, 
    hoursCount: Double,
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    Card(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thống kê hôm nay", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(value = "$totalSessions", label = "Tiết học", color = Primary)
                StatItem(value = "$totalExams",  label = "Kỳ thi",   color = ExamRed)
                StatItem(value = "$subjectsCount", label = "Môn học", color = SuccessGreen)
                StatItem(value = "%.1f".format(hoursCount), label = "Giờ học", color = Tertiary)
            }
        }
    }
}

@Composable
private fun DetailDialog(session: ClassSession?, exam: ExamReminder?, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (session != null) {
                    Text("Chi tiết buổi học", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(20.dp))
                    DetailItem(Icons.Default.Book, "Môn học", session.subject)
                    DetailItem(Icons.Default.Person, "Giảng viên", session.teacher)
                    DetailItem(Icons.Default.Room, "Phòng học", session.room)
                    DetailItem(Icons.Default.AccessTime, "Thời gian", "${session.startTime} - ${session.endTime}")
                } else if (exam != null) {
                    Text("Chi tiết kỳ thi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = ExamRed)
                    Spacer(Modifier.height(20.dp))
                    DetailItem(Icons.Default.Assignment, "Môn thi", exam.subject)
                    DetailItem(Icons.Default.Category, "Loại hình", exam.examType.label)
                    DetailItem(Icons.Default.CalendarToday, "Ngày thi", exam.date)
                    DetailItem(Icons.Default.AccessTime, "Giờ thi", exam.time)
                    DetailItem(Icons.Default.Room, "Phòng thi", exam.room)
                    if (exam.notes.isNotEmpty()) {
                        DetailItem(Icons.Default.Notes, "Ghi chú", exam.notes)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Đóng")
                }
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HomeHeader() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when(hour) {
        in 0..11 -> "Chào buổi sáng!"
        in 12..17 -> "Chào buổi chiều!"
        else -> "Chào buổi tối!"
    }
    val dateString = "Ngày ${calendar.get(Calendar.DAY_OF_MONTH)} tháng ${calendar.get(Calendar.MONTH) + 1}"

    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(Primary, Secondary))).padding(24.dp)
    ) {
        Icon(Icons.Default.AutoStories, null, tint = Color.White.copy(alpha = 0.1f), modifier = Modifier.size(140.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp))
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Spacer(Modifier.height(32.dp))
            Text(text = greeting, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
            Text(text = "Bạn học hôm nay thế nào?", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = dateString, style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MotivationCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, null, tint = WarningAmber, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Góc động lực", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("\"Hành trình vạn dặm bắt đầu từ một bước chân nhỏ. Hãy cố gắng học tập nhé!\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun QuickActionsRow(onScheduleClick: () -> Unit, onExamsClick: () -> Unit, onManageClick: () -> Unit, onAboutClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Lịch học", "Xem khóa biểu", Icons.AutoMirrored.Filled.MenuBook, listOf(Color(0xFF4F46E5), Color(0xFF7C3AED)), Modifier.weight(1f), onScheduleClick)
            QuickActionCard("Nhắc thi", "Quản lý thi", Icons.Filled.Alarm, listOf(Color(0xFF0891B2), Color(0xFF0E7490)), Modifier.weight(1f), onExamsClick)
        }
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Cập nhật", "Thêm/Sửa/Xóa", Icons.Filled.Settings, listOf(Color(0xFF10B981), Color(0xFF059669)), Modifier.weight(1f), onManageClick)
            QuickActionCard("Thông tin", "Về ứng dụng", Icons.Filled.Info, listOf(Color(0xFFF59E0B), Color(0xFFD97706)), Modifier.weight(1f), onAboutClick)
        }
    }
}

@Composable
private fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, gradient: List<Color>, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick  = onClick, modifier = modifier.height(100.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors = gradient)).padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.25f), modifier = Modifier.size(52.dp).align(Alignment.BottomEnd))
            Column {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TodayClassCard(session: ClassSession, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = SubjectColors[session.colorIndex % SubjectColors.size]
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(56.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(52.dp)) {
                Text(text = session.startTime, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
                Text(text = "|", color = MaterialTheme.colorScheme.outlineVariant, fontSize = 10.sp)
                Text(text = session.endTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = session.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(text = session.teacher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Room, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(text = session.room, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun UpcomingExamCard(exam: ExamReminder, onClick: () -> Unit) {
    val color = SubjectColors[exam.colorIndex % SubjectColors.size]
    val urgentColor = when {
        exam.daysUntil <= 3 -> ExamRed
        exam.daysUntil <= 7 -> WarningAmber
        else -> SuccessGreen
    }
    Card(onClick = onClick, modifier = Modifier.width(180.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Spacer(Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
                Text(text = exam.examType.label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = exam.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(text = exam.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            val statusText = if (exam.daysUntil == 0) "Hôm nay!" else "Còn ${exam.daysUntil} ngày"
            Surface(shape = RoundedCornerShape(20.dp), color = urgentColor.copy(alpha = 0.12f)) {
                Text(text = statusText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = urgentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyStateCard(message: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

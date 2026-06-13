package com.example.studysync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.studysync.data.*
import com.example.studysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    sessions: List<ClassSession>,
    exams: List<ExamReminder>,
    onBack: () -> Unit
) {
    // Chỉ tính toán trên các kỳ thi chưa diễn ra hoặc đang diễn ra hôm nay
    val upcomingExams = exams.filter { it.daysUntil >= 0 }
    
    val totalSessions  = sessions.size
    val totalExams     = upcomingExams.size
    val uniqueSubjects = (sessions.map { it.subject } + upcomingExams.map { it.subject }).distinct().size
    val urgentExams    = upcomingExams.count { it.daysUntil in 0..7 }
    
    val sessionsByDay  = DayOfWeek.entries.associateWith { day -> sessions.count { it.dayOfWeek == day } }
    val maxDayCount    = sessionsByDay.values.maxOrNull()?.takeIf { it > 0 } ?: 1
    
    // Kỳ thi gần nhất (phải là kỳ thi sắp tới)
    val nextExam       = upcomingExams.minByOrNull { it.daysUntil }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Học Tập", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryCard("Buổi học", "$totalSessions", Icons.AutoMirrored.Filled.MenuBook, Primary, Modifier.weight(1f))
                    SummaryCard("Kỳ thi", "$totalExams", Icons.AutoMirrored.Filled.Assignment, ExamRed, Modifier.weight(1f))
                    SummaryCard("Môn học", "$uniqueSubjects", Icons.Outlined.School, SuccessGreen, Modifier.weight(1f))
                    SummaryCard("Sắp thi", "$urgentExams", Icons.Outlined.Alarm, WarningAmber, Modifier.weight(1f))
                }
            }

            if (nextExam != null) {
                item { CountdownCard(exam = nextExam) }
            }

            item {
                StatCard(title = "Lịch học theo ngày", icon = Icons.Outlined.CalendarMonth) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sessionsByDay.forEach { (day, count) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(day.short, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(28.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                    if (count > 0) {
                                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(count.toFloat() / maxDayCount).clip(RoundedCornerShape(4.dp)).background(Brush.horizontalGradient(listOf(Primary, Secondary))))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                }
            }

            if (upcomingExams.isNotEmpty()) {
                item {
                    StatCard(title = "Danh sách kỳ thi sắp tới", icon = Icons.Outlined.Alarm) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            upcomingExams.sortedBy { it.daysUntil }.forEach { exam ->
                                val urgentColor = when {
                                    exam.daysUntil <= 3 -> ExamRed
                                    exam.daysUntil <= 7 -> WarningAmber
                                    else                -> SuccessGreen
                                }
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.width(3.dp).height(36.dp).clip(RoundedCornerShape(2.dp)).background(SubjectColors[exam.colorIndex % SubjectColors.size]))
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exam.subject, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("${exam.examType.label} • ${exam.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(shape = RoundedCornerShape(20.dp), color = urgentColor.copy(alpha = 0.12f)) {
                                        Text("Còn ${exam.daysUntil}N", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = urgentColor, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (exam != upcomingExams.sortedBy { it.daysUntil }.last()) {
                                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(20.dp), tint = color)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CountdownCard(exam: ExamReminder) {
    val urgentColor = when {
        exam.daysUntil <= 3 -> ExamRed
        exam.daysUntil <= 7 -> WarningAmber
        else                -> SuccessGreen
    }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(urgentColor.copy(alpha = 0.8f), urgentColor))).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kỳ thi gần nhất", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(4.dp))
                    Text(exam.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${exam.examType.label} • ${exam.date} ${exam.time}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${exam.daysUntil}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("ngày nữa", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

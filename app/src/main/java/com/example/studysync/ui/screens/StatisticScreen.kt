package com.example.studysync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    // Computed stats
    val totalSessions  = sessions.size
    val totalExams     = exams.size
    val uniqueSubjects = (sessions.map { it.subject } + exams.map { it.subject }).distinct().size
    val urgentExams    = exams.count { it.daysUntil <= 7 }
    val subjectSessionCount = sessions.groupBy { it.subject }.mapValues { it.value.size }
    val examsByType    = exams.groupBy { it.examType }.mapValues { it.value.size }
    val sessionsByDay  = DayOfWeek.entries.associateWith { day -> sessions.count { it.dayOfWeek == day } }
    val maxDayCount    = sessionsByDay.values.maxOrNull()?.takeIf { it > 0 } ?: 1
    val nextExam       = exams.minByOrNull { it.daysUntil }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Học Tập") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary cards row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryCard("Buổi học", "$totalSessions", Icons.Outlined.MenuBook, Primary, Modifier.weight(1f))
                    SummaryCard("Kỳ thi", "$totalExams", Icons.Outlined.Assignment, ExamRed, Modifier.weight(1f))
                    SummaryCard("Môn học", "$uniqueSubjects", Icons.Outlined.School, SuccessGreen, Modifier.weight(1f))
                    SummaryCard("Sắp thi", "$urgentExams", Icons.Outlined.Alarm, WarningAmber, Modifier.weight(1f))
                }
            }

            // Countdown to next exam
            if (nextExam != null) {
                item { CountdownCard(exam = nextExam) }
            }

            // Sessions by day of week bar chart
            item {
                StatCard(title = "Lịch học theo ngày", icon = Icons.Outlined.CalendarMonth) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sessionsByDay.forEach { (day, count) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    day.short,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.width(28.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    if (count > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(count.toFloat() / maxDayCount)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    Brush.horizontalGradient(listOf(Primary, Secondary))
                                                )
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "$count",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Exams by type
            if (examsByType.isNotEmpty()) {
                item {
                    StatCard(title = "Kỳ thi theo loại", icon = Icons.Outlined.Assignment) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            examsByType.entries.forEach { (type, count) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$count",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ExamRed
                                    )
                                    Text(
                                        type.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subject breakdown
            if (subjectSessionCount.isNotEmpty()) {
                item {
                    StatCard(title = "Số buổi theo môn", icon = Icons.Outlined.BarChart) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            subjectSessionCount.entries
                                .sortedByDescending { it.value }
                                .forEachIndexed { idx, (subject, count) ->
                                    val color = SubjectColors[idx % SubjectColors.size]
                                    val maxCount = subjectSessionCount.values.maxOrNull()?.takeIf { it > 0 } ?: 1
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(color)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            subject,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(14.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(count.toFloat() / maxCount)
                                                    .clip(RoundedCornerShape(7.dp))
                                                    .background(color)
                                            )
                                        }
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            "$count",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = color
                                        )
                                    }
                                }
                        }
                    }
                }
            }

            // Upcoming exams list
            if (exams.isNotEmpty()) {
                item {
                    StatCard(title = "Danh sách kỳ thi sắp tới", icon = Icons.Outlined.Alarm) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            exams.sortedBy { it.daysUntil }.forEach { exam ->
                                val urgentColor = when {
                                    exam.daysUntil <= 3 -> ExamRed
                                    exam.daysUntil <= 7 -> WarningAmber
                                    else                -> SuccessGreen
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp).height(40.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(SubjectColors[exam.colorIndex % SubjectColors.size])
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exam.subject, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("${exam.examType.label} • ${exam.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(shape = RoundedCornerShape(20.dp), color = urgentColor.copy(alpha = 0.12f)) {
                                        Text(
                                            "Còn ${exam.daysUntil}N",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = urgentColor, fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (exam != exams.sortedBy { it.daysUntil }.last()) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(urgentColor.copy(alpha = 0.8f), urgentColor)))
                .padding(16.dp)
        ) {
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
private fun StatCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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

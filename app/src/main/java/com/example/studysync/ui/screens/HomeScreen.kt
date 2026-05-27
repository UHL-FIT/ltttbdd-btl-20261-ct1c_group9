package com.example.studysync.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@Composable
fun HomeScreen(
    sessions: List<ClassSession>,
    exams: List<ExamReminder>,
    onNavigateToSchedule: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToManage: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                QuickActionsRow(
                    onScheduleClick = onNavigateToSchedule,
                    onExamsClick = onNavigateToExams,
                    onManageClick = onNavigateToManage,
                    onAboutClick = onNavigateToAbout
                )
            }
            // ... (keep rest of items)

        item {
            SectionTitle(
                title = "Lịch hôm nay",
                subtitle = "Thứ 2 - Dự kiến",
                icon = Icons.Default.CalendarToday,
                onSeeAll = onNavigateToSchedule
            )
        }

        if (sessions.isEmpty()) {
            item { EmptyStateCard(message = "Hôm nay bạn không có lịch học!") }
        } else {
            items(sessions) { session ->
                TodayClassCard(session = session)
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            SectionTitle(
                title = "Kỳ thi sắp tới",
                subtitle = "${exams.size} kỳ thi",
                icon = Icons.Default.Alarm,
                onSeeAll = onNavigateToExams
            )
            Spacer(Modifier.height(4.dp))

            if (exams.isEmpty()) {
                EmptyStateCard(message = "Không có kỳ thi nào sắp tới.")
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(exams) { exam ->
                        UpcomingExamCard(exam = exam)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            StudyStatsSection()
        }
    }
}
}

@Composable
private fun QuickActionsRow(
    onScheduleClick: () -> Unit,
    onExamsClick: () -> Unit,
    onManageClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                title    = "Lịch học",
                subtitle = "Xem thời khóa biểu",
                icon     = Icons.AutoMirrored.Filled.MenuBook,
                gradient = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED)),
                modifier = Modifier.weight(1f),
                onClick  = onScheduleClick
            )
            QuickActionCard(
                title    = "Nhắc thi",
                subtitle = "Quản lý kỳ thi",
                icon     = Icons.Filled.Alarm,
                gradient = listOf(Color(0xFF0891B2), Color(0xFF0E7490)),
                modifier = Modifier.weight(1f),
                onClick  = onExamsClick
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                title    = "Nhập liệu",
                subtitle = "Thêm môn & lịch thi",
                icon     = Icons.Filled.Edit,
                gradient = listOf(Color(0xFF16A34A), Color(0xFF15803D)),
                modifier = Modifier.weight(1f),
                onClick  = onManageClick
            )
            QuickActionCard(
                title    = "Giới thiệu",
                subtitle = "Thông tin nhóm",
                icon     = Icons.Filled.Info,
                gradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                modifier = Modifier.weight(1f),
                onClick  = onAboutClick
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick  = onClick,
        modifier = modifier.height(100.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors = gradient))
                .padding(16.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.25f),
                modifier           = Modifier.size(52.dp).align(Alignment.BottomEnd)
            )
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
private fun SectionTitle(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = onSeeAll) { Text("Xem tất cả", style = MaterialTheme.typography.labelMedium) }
    }
}

@Composable
fun TodayClassCard(session: ClassSession, modifier: Modifier = Modifier) {
    val color = SubjectColors[session.colorIndex % SubjectColors.size]
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
                    Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(text = session.teacher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Room, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(text = session.room, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun UpcomingExamCard(exam: ExamReminder) {
    val color = SubjectColors[exam.colorIndex % SubjectColors.size]
    val urgentColor = when {
        exam.daysUntil <= 3  -> ExamRed
        exam.daysUntil <= 7  -> WarningAmber
        else                 -> SuccessGreen
    }
    Card(
        modifier  = Modifier.width(180.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
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
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(text = exam.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = urgentColor.copy(alpha = 0.12f)) {
                Text(text = "Còn ${exam.daysUntil} ngày", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = urgentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StudyStatsSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thống kê tuần này", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(value = "12", label = "Buổi học", color = Primary)
                StatItem(value = "8",  label = "Kỳ thi",   color = ExamRed)
                StatItem(value = "3",  label = "Môn học",  color = SuccessGreen)
                StatItem(value = "42", label = "Giờ học",  color = Tertiary)
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall,     color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyStateCard(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
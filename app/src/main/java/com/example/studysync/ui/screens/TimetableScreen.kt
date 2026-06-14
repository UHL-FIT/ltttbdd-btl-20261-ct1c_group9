package com.example.studysync.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studysync.data.*
import com.example.studysync.ui.components.*
import com.example.studysync.ui.theme.SubjectColors
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    sessions: List<ClassSession>,
    onAddSession: (ClassSession) -> Unit,
    onDeleteSession: (String) -> Unit,
    onBack: () -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSessionForEdit by remember { mutableStateOf<ClassSession?>(null) }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    
    var showStatusDialog by remember { mutableStateOf(false) }
    var dialogStatus by remember { mutableStateOf(true) }
    var dialogMessage by remember { mutableStateOf("") }

    val sessionsByDay = DayOfWeek.entries.associateWith { day ->
        sessions.filter { it.dayOfWeek == day }.sortedBy { it.startTime }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch Học Theo Tuần") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm lịch học")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Day selector tabs
            ScrollableTabRow(
                selectedTabIndex = DayOfWeek.entries.indexOf(selectedDay),
                edgePadding = 8.dp
            ) {
                DayOfWeek.entries.forEach { day ->
                    val count = sessionsByDay[day]?.size ?: 0
                    Tab(
                        selected = selectedDay == day,
                        onClick  = { selectedDay = day },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day.short, fontWeight = FontWeight.Bold)
                                if (count > 0) {
                                    Badge { Text(count.toString()) }
                                }
                            }
                        }
                    )
                }
            }

            val daySessions = sessionsByDay[selectedDay] ?: emptyList()
            if (daySessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Không có lịch học ${selectedDay.displayVi}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Thêm lịch học")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(daySessions, key = { it.id }) { session ->
                        SessionCard(
                            session = session, 
                            onDelete = { onDeleteSession(session.id) },
                            onEdit = { selectedSessionForEdit = session }
                        )
                    }
                }
            }
        }
        
        if (showStatusDialog) {
            StatusDialog(
                isSuccess = dialogStatus,
                message = dialogMessage,
                onDismiss = { showStatusDialog = false }
            )
        }
    }

    if (showAddDialog) {
        SessionFormDialog(
            initialDay = selectedDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { session ->
                onAddSession(session)
                dialogStatus = true
                dialogMessage = "Đã thêm môn học thành công!"
                showStatusDialog = true
                showAddDialog = false
            },
            onError = { msg ->
                dialogStatus = false
                dialogMessage = msg
                showStatusDialog = true
            }
        )
    }

    if (selectedSessionForEdit != null) {
        SessionFormDialog(
            session = selectedSessionForEdit,
            onDismiss = { selectedSessionForEdit = null },
            onConfirm = { session ->
                onAddSession(session) // Room insert replaces by ID
                dialogStatus = true
                dialogMessage = "Đã cập nhật môn học thành công!"
                showStatusDialog = true
                selectedSessionForEdit = null
            },
            onError = { msg ->
                dialogStatus = false
                dialogMessage = msg
                showStatusDialog = true
            }
        )
    }
}

@Composable
private fun SessionCard(session: ClassSession, onDelete: () -> Unit, onEdit: () -> Unit) {
    val color = SubjectColors[session.colorIndex % SubjectColors.size]
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEdit
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(64.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.width(56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(session.startTime, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
                Text("—", color = MaterialTheme.colorScheme.outlineVariant)
                Text(session.endTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(session.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(session.teacher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Room, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(session.room, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xóa lịch học?") },
            text  = { Text("Bạn có chắc muốn xóa môn \"${session.subject}\" không?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") }
            }
        )
    }
}

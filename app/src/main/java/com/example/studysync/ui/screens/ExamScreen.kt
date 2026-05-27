package com.example.studysync.ui.screens

<<<<<<< HEAD
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
=======
import androidx.compose.foundation.*
>>>>>>> 4979ae1e5a2f11e41f3498f5f4bec752a1672627
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
<<<<<<< HEAD
=======
import androidx.compose.material.icons.outlined.*
>>>>>>> 4979ae1e5a2f11e41f3498f5f4bec752a1672627
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.studysync.data.*
import com.example.studysync.ui.StudyViewModel
import com.example.studysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(navController: NavController, viewModel: StudyViewModel) {
    val examList by viewModel.allExams.collectAsState()
    var selectedExam by remember { mutableStateOf<ExamReminder?>(null) }
    var showDialog by remember { mutableStateOf(false) }
=======
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studysync.data.*
import com.example.studysync.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    exams: List<ExamReminder>,
    onAddExam: (ExamReminder) -> Unit,
    onDeleteExam: (String) -> Unit,
    onViewStats: () -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val sorted = exams.sortedBy { it.daysUntil }
>>>>>>> 4979ae1e5a2f11e41f3498f5f4bec752a1672627

    Scaffold(
        topBar = {
            TopAppBar(
<<<<<<< HEAD
                title = { Text("Danh Sách Lịch Thi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (examList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có lịch thi nào được cập nhật", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(examList) { exam ->
                        ExamItemCard(exam = exam, onClick = { 
                            selectedExam = exam
                            showDialog = true 
                        })
                    }
                }
            }

            if (showDialog && selectedExam != null) {
                ExamDetailDialog(
                    exam = selectedExam!!, 
                    viewModel = viewModel,
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun ExamItemCard(exam: ExamReminder, onClick: () -> Unit) {
    val color = SubjectColors[exam.colorIndex % SubjectColors.size]
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = exam.examType.label,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = exam.subject,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                IconLabel(Icons.Default.CalendarToday, exam.date)
                IconLabel(Icons.Default.AccessTime, exam.time)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                IconLabel(Icons.Default.Room, exam.room)
                Text(
                    text = "Xem chi tiết",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun IconLabel(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text = text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ExamDetailDialog(exam: ExamReminder, viewModel: StudyViewModel, onDismiss: () -> Unit) {
    val color = SubjectColors[exam.colorIndex % SubjectColors.size]
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = exam.subject,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = exam.examType.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(Modifier.height(20.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.1f))
                Spacer(Modifier.height(20.dp))

                DetailRow(icon = Icons.Default.CalendarToday, label = "Ngày thi", value = exam.date)
                DetailRow(icon = Icons.Default.AccessTime, label = "Giờ thi", value = exam.time)
                DetailRow(icon = Icons.Default.Room, label = "Phòng thi", value = exam.room)
                
                if (exam.notes.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Ghi chú:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(exam.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteExam(exam)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteOutline, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Xóa")
                    }
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đóng")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
=======
                title = { Text("Nhắc Thi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onViewStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Thống kê")
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm nhắc thi")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm nhắc thi")
            }
        }
    ) { padding ->
        if (sorted.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.EventAvailable,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Chưa có lịch thi nào", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Thêm nhắc thi")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sorted, key = { it.id }) { exam ->
                    ExamCard(exam = exam, onDelete = { onDeleteExam(exam.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddExamDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { exam ->
                onAddExam(exam)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ExamCard(exam: ExamReminder, onDelete: () -> Unit) {
    val color = SubjectColors[exam.colorIndex % SubjectColors.size]
    val urgentColor = when {
        exam.daysUntil <= 3 -> ExamRed
        exam.daysUntil <= 7 -> WarningAmber
        else                -> SuccessGreen
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
                Spacer(Modifier.width(8.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
                    Text(
                        exam.examType.label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = urgentColor.copy(alpha = 0.12f)) {
                    Text(
                        "Còn ${exam.daysUntil} ngày",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = urgentColor, fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(exam.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip(icon = Icons.Outlined.CalendarMonth, text = exam.date)
                InfoChip(icon = Icons.Outlined.Schedule, text = exam.time)
                InfoChip(icon = Icons.Outlined.Room, text = exam.room)
            }
            if (exam.notes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notes, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(exam.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xóa nhắc thi?") },
            text  = { Text("Bạn có chắc muốn xóa lịch thi \"${exam.subject}\" không?") },
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

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExamDialog(
    onDismiss: () -> Unit,
    onConfirm: (ExamReminder) -> Unit
) {
    var subject    by remember { mutableStateOf("") }
    var date       by remember { mutableStateOf("") }
    var time       by remember { mutableStateOf("") }
    var room       by remember { mutableStateOf("") }
    var notes      by remember { mutableStateOf("") }
    var daysUntil  by remember { mutableStateOf("") }
    var examType   by remember { mutableStateOf(ExamType.MIDTERM) }
    var colorIndex by remember { mutableStateOf(0) }
    var typeExpanded by remember { mutableStateOf(false) }

    val isValid = subject.isNotBlank() && date.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm nhắc thi", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject, onValueChange = { subject = it },
                    label = { Text("Tên môn thi *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                // Exam type dropdown
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = examType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hình thức thi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        ExamType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                onClick = { examType = type; typeExpanded = false }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date, onValueChange = { date = it },
                        label = { Text("Ngày thi *") },
                        placeholder = { Text("28/05/2026") },
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                    OutlinedTextField(
                        value = time, onValueChange = { time = it },
                        label = { Text("Giờ thi") },
                        placeholder = { Text("07:30") },
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                }
                OutlinedTextField(
                    value = room, onValueChange = { room = it },
                    label = { Text("Phòng thi") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = daysUntil, onValueChange = { daysUntil = it.filter { c -> c.isDigit() } },
                    label = { Text("Số ngày còn lại") },
                    placeholder = { Text("7") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3
                )
                // Color picker
                Text("Màu sắc:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectColors.forEachIndexed { idx, color ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(color)
                                .border(
                                    width = if (colorIndex == idx) 3.dp else 0.dp,
                                    color = if (colorIndex == idx) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { colorIndex = idx }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ExamReminder(
                            id         = UUID.randomUUID().toString(),
                            subject    = subject.trim(),
                            examType   = examType,
                            date       = date.trim(),
                            time       = time.trim(),
                            room       = room.trim(),
                            notes      = notes.trim(),
                            daysUntil  = daysUntil.toIntOrNull() ?: 0,
                            colorIndex = colorIndex
                        )
                    )
                },
                enabled = isValid
            ) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
>>>>>>> 4979ae1e5a2f11e41f3498f5f4bec752a1672627
}

package com.example.studysync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studysync.data.*
import com.example.studysync.ui.theme.SubjectColors
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    onAddSession: (ClassSession) -> Unit,
    onAddExam: (ExamReminder) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Thêm Môn Học", "Thêm Lịch Thi")
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Nhập Liệu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Medium) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.AutoMirrored.Filled.MenuBook else Icons.Default.Alarm,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> AddSessionForm(
                    onSave = { session ->
                        onAddSession(session)
                        snackbarMessage = "Đã thêm môn học \"${session.subject}\""
                    }
                )
                1 -> AddExamForm(
                    onSave = { exam ->
                        onAddExam(exam)
                        snackbarMessage = "Đã thêm lịch thi \"${exam.subject}\""
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSessionForm(onSave: (ClassSession) -> Unit) {
    var subject     by remember { mutableStateOf("") }
    var teacher     by remember { mutableStateOf("") }
    var room        by remember { mutableStateOf("") }
    var startTime   by remember { mutableStateOf("") }
    var endTime     by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var colorIndex  by remember { mutableStateOf(0) }
    var dayExpanded by remember { mutableStateOf(false) }

    val isValid = subject.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormSectionHeader(icon = Icons.AutoMirrored.Filled.MenuBook, title = "Thông tin môn học")

        OutlinedTextField(
            value = subject, onValueChange = { subject = it },
            label = { Text("Tên môn học *") },
            leadingIcon = { Icon(Icons.Default.School, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = teacher, onValueChange = { teacher = it },
            label = { Text("Giảng viên") },
            leadingIcon = { Icon(Icons.Default.Person, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = room, onValueChange = { room = it },
            label = { Text("Phòng học") },
            leadingIcon = { Icon(Icons.Default.Room, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        FormSectionHeader(icon = Icons.Default.Schedule, title = "Thời gian")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = startTime, onValueChange = { startTime = it },
                label = { Text("Giờ bắt đầu *") },
                placeholder = { Text("07:30") },
                modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = endTime, onValueChange = { endTime = it },
                label = { Text("Giờ kết thúc *") },
                placeholder = { Text("09:10") },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }

        ExposedDropdownMenuBox(expanded = dayExpanded, onExpandedChange = { dayExpanded = it }) {
            OutlinedTextField(
                value = selectedDay.displayVi,
                onValueChange = {},
                readOnly = true,
                label = { Text("Thứ trong tuần") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, Modifier.size(20.dp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                DayOfWeek.entries.forEach { day ->
                    DropdownMenuItem(
                        text = { Text(day.displayVi) },
                        onClick = { selectedDay = day; dayExpanded = false }
                    )
                }
            }
        }

        FormSectionHeader(icon = Icons.Default.Palette, title = "Màu sắc")
        ColorPicker(selectedIndex = colorIndex, onSelect = { colorIndex = it })

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                onSave(
                    ClassSession(
                        id = UUID.randomUUID().toString(),
                        subject = subject.trim(),
                        teacher = teacher.trim(),
                        room = room.trim(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim(),
                        dayOfWeek = selectedDay,
                        colorIndex = colorIndex
                    )
                )
                subject = ""; teacher = ""; room = ""; startTime = ""; endTime = ""
                selectedDay = DayOfWeek.MONDAY; colorIndex = 0
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Lưu Môn Học", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExamForm(onSave: (ExamReminder) -> Unit) {
    var subject      by remember { mutableStateOf("") }
    var date         by remember { mutableStateOf("") }
    var time         by remember { mutableStateOf("") }
    var room         by remember { mutableStateOf("") }
    var notes        by remember { mutableStateOf("") }
    var daysUntil    by remember { mutableStateOf("") }
    var examType     by remember { mutableStateOf(ExamType.MIDTERM) }
    var colorIndex   by remember { mutableStateOf(0) }
    var typeExpanded by remember { mutableStateOf(false) }

    val isValid = subject.isNotBlank() && date.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormSectionHeader(icon = Icons.Default.Alarm, title = "Thông tin kỳ thi")

        OutlinedTextField(
            value = subject, onValueChange = { subject = it },
            label = { Text("Tên môn thi *") },
            leadingIcon = { Icon(Icons.Default.School, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
            OutlinedTextField(
                value = examType.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hình thức thi") },
                leadingIcon = { Icon(Icons.Default.Assignment, null, Modifier.size(20.dp)) },
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

        FormSectionHeader(icon = Icons.Default.Schedule, title = "Thời gian & Địa điểm")

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
            leadingIcon = { Icon(Icons.Default.Room, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = daysUntil, onValueChange = { daysUntil = it.filter { c -> c.isDigit() } },
            label = { Text("Số ngày còn lại") },
            leadingIcon = { Icon(Icons.Default.HourglassBottom, null, Modifier.size(20.dp)) },
            placeholder = { Text("7") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = notes, onValueChange = { notes = it },
            label = { Text("Ghi chú") },
            leadingIcon = { Icon(Icons.Default.Notes, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), maxLines = 3
        )

        FormSectionHeader(icon = Icons.Default.Palette, title = "Màu sắc")
        ColorPicker(selectedIndex = colorIndex, onSelect = { colorIndex = it })

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                onSave(
                    ExamReminder(
                        id = UUID.randomUUID().toString(),
                        subject = subject.trim(),
                        examType = examType,
                        date = date.trim(),
                        time = time.trim(),
                        room = room.trim(),
                        notes = notes.trim(),
                        daysUntil = daysUntil.toIntOrNull() ?: 0,
                        colorIndex = colorIndex
                    )
                )
                subject = ""; date = ""; time = ""; room = ""; notes = ""; daysUntil = ""
                examType = ExamType.MIDTERM; colorIndex = 0
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Lưu Lịch Thi", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FormSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
private fun ColorPicker(selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SubjectColors.forEachIndexed { idx, color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .clickable { onSelect(idx) },
                contentAlignment = Alignment.Center
            ) {
                if (selectedIndex == idx) {
                    Icon(Icons.Default.Check, null, Modifier.size(16.dp), tint = Color.White)
                }
            }
        }
    }
}

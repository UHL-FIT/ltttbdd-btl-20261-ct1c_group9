package com.example.studysync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studysync.data.*
import com.example.studysync.ui.StudyViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(navController: NavController, viewModel: StudyViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lịch học", "Lịch thi")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm thông tin mới", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedTabIndex == 0) {
                    SessionForm(onSave = { session ->
                        viewModel.addSession(session)
                        navController.popBackStack()
                    })
                } else {
                    ExamForm(onSave = { exam ->
                        viewModel.addExam(exam)
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}

@Composable
fun SessionForm(onSave: (ClassSession) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("07:30") }
    var endTime by remember { mutableStateOf("09:10") }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextFieldWithIcon("Tên môn học", subject, Icons.Default.Book) { subject = it }
        OutlinedTextFieldWithIcon("Giảng viên", teacher, Icons.Default.Person) { teacher = it }
        OutlinedTextFieldWithIcon("Phòng học", room, Icons.Default.Room) { room = it }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextFieldWithIcon("Bắt đầu", startTime, Icons.Default.AccessTime, Modifier.weight(1f)) { startTime = it }
            OutlinedTextFieldWithIcon("Kết thúc", endTime, Icons.Default.AccessTime, Modifier.weight(1f)) { endTime = it }
        }

        Text("Chọn ngày trong tuần", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.values().forEach { day ->
                FilterChip(
                    selected = selectedDay == day,
                    onClick = { selectedDay = day },
                    label = { Text(day.displayVi) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (subject.isNotBlank()) {
                    onSave(ClassSession(UUID.randomUUID().toString(), subject, teacher, room, startTime, endTime, selectedDay, (0..7).random()))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Save, null)
            Spacer(Modifier.width(8.dp))
            Text("Lưu buổi học")
        }
    }
}

@Composable
fun ExamForm(onSave: (ExamReminder) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ExamType.QUIZ) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextFieldWithIcon("Tên môn thi", subject, Icons.Default.Assignment) { subject = it }
        OutlinedTextFieldWithIcon("Ngày thi (VD: 25/05/2026)", date, Icons.Default.CalendarToday) { date = it }
        OutlinedTextFieldWithIcon("Giờ thi", time, Icons.Default.AccessTime) { time = it }
        OutlinedTextFieldWithIcon("Phòng thi", room, Icons.Default.Room) { room = it }
        OutlinedTextFieldWithIcon("Ghi chú", notes, Icons.Default.Notes) { notes = it }

        Text("Loại hình thi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExamType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.label) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (subject.isNotBlank()) {
                    onSave(ExamReminder(UUID.randomUUID().toString(), subject, selectedType, date, time, room, notes, (1..30).random(), (0..7).random()))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Save, null)
            Spacer(Modifier.width(8.dp))
            Text("Lưu lịch thi")
        }
    }
}

@Composable
fun OutlinedTextFieldWithIcon(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}

package com.example.studysync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studysync.data.*
import com.example.studysync.ui.StudyViewModel
import com.example.studysync.ui.components.*
import com.example.studysync.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(navController: NavController, viewModel: StudyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Thêm Môn Học", "Thêm Lịch Thi")
    
    var showStatusDialog by remember { mutableStateOf(false) }
    var dialogStatus by remember { mutableStateOf(true) }
    var dialogMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Nhập Liệu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) },
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    AddSessionForm(onSave = { session ->
                        viewModel.addSession(session)
                        dialogStatus = true
                        dialogMessage = "Đã thêm môn học \"${session.subject}\" thành công!"
                        showStatusDialog = true
                    }, onError = { msg ->
                        dialogStatus = false
                        dialogMessage = msg
                        showStatusDialog = true
                    })
                } else {
                    AddExamForm(onSave = { exam ->
                        viewModel.addExam(exam)
                        dialogStatus = true
                        dialogMessage = "Đã thêm lịch thi \"${exam.subject}\" thành công!"
                        showStatusDialog = true
                    }, onError = { msg ->
                        dialogStatus = false
                        dialogMessage = msg
                        showStatusDialog = true
                    })
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
}

private fun calculateDaysUntil(dateStr: String): Int? {
    if (dateStr.length < 5) return null
    return try {
        val cleanDate = if (dateStr.endsWith("/")) dateStr.dropLast(1) else dateStr
        val finalDateStr = if (cleanDate.length == 5) {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            "$cleanDate/$currentYear"
        } else cleanDate

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        
        val examDate = sdf.parse(finalDateStr) ?: return null
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.time

        val diff = examDate.time - today.time
        (diff / (24 * 60 * 60 * 1000)).toInt()
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSessionForm(onSave: (ClassSession) -> Unit, onError: (String) -> Unit) {
    var subject     by remember { mutableStateOf("") }
    var teacher     by remember { mutableStateOf("") }
    var room        by remember { mutableStateOf("") }
    var startTime   by remember { mutableStateOf("") }
    var endTime     by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var colorIndex  by remember { mutableIntStateOf(0) }
    var dayExpanded by remember { mutableStateOf(false) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker   by remember { mutableStateOf(false) }

    fun isTimeLogical(): Boolean {
        if (startTime.length == 5 && endTime.length == 5) {
            val startParts = startTime.split(":").map { it.toInt() }
            val endParts = endTime.split(":").map { it.toInt() }
            val startTotal = startParts[0] * 60 + startParts[1]
            val endTotal = endParts[0] * 60 + endParts[1]
            return startTotal < endTotal
        }
        return true
    }

    val isValid = subject.isNotBlank() && startTime.length == 5 && endTime.length == 5

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormSectionHeader(icon = Icons.AutoMirrored.Filled.MenuBook, title = "Thông tin môn học")

        OutlinedTextField(
            value = subject, onValueChange = { subject = it },
            label = { Text("Tên môn học *") },
            leadingIcon = { Icon(Icons.Default.School, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = teacher, onValueChange = { teacher = it },
            label = { Text("Giảng viên") },
            leadingIcon = { Icon(Icons.Default.Person, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = room, onValueChange = { room = it },
            label = { Text("Phòng học") },
            leadingIcon = { Icon(Icons.Default.Room, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        FormSectionHeader(icon = Icons.Default.Schedule, title = "Thời gian")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = startTime, 
                onValueChange = { },
                readOnly = true,
                label = { Text("Giờ bắt đầu *") },
                placeholder = { Text("07:30") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showStartTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = endTime, 
                onValueChange = { },
                readOnly = true,
                label = { Text("Giờ kết thúc *") },
                placeholder = { Text("09:10") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showEndTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (showStartTimePicker) {
            TimePickerModal(
                onTimeSelected = { h: Int, m: Int -> 
                    startTime = "%02d:%02d".format(h, m)
                    showStartTimePicker = false
                },
                onDismiss = { showStartTimePicker = false }
            )
        }
        if (showEndTimePicker) {
            TimePickerModal(
                onTimeSelected = { h: Int, m: Int -> 
                    endTime = "%02d:%02d".format(h, m)
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false }
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
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp)
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

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isTimeLogical()) {
                    onError("Giờ kết thúc phải sau giờ bắt đầu!")
                    return@Button
                }
                if (subject.any { !it.isLetterOrDigit() && !it.isWhitespace() && it != '-' && it != '_' }) {
                    onError("Tên môn học chứa ký tự không hợp lệ!")
                    return@Button
                }
                if (teacher.isNotBlank() && teacher.any { !it.isLetter() && !it.isWhitespace() && it != '.' }) {
                    onError("Tên giảng viên chỉ được chứa chữ cái và dấu chấm!")
                    return@Button
                }

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
private fun AddExamForm(onSave: (ExamReminder) -> Unit, onError: (String) -> Unit) {
    val currentDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    var subject      by remember { mutableStateOf("") }
    var date         by remember { mutableStateOf(currentDate) }
    var time         by remember { mutableStateOf("") }
    var room         by remember { mutableStateOf("") }
    var notes        by remember { mutableStateOf("") }
    var examType     by remember { mutableStateOf(ExamType.MIDTERM) }
    var colorIndex   by remember { mutableIntStateOf(0) }
    var typeExpanded by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val daysUntilResult = remember(date) { calculateDaysUntil(date) }
    val isDateValid = date.isEmpty() || daysUntilResult != null
    val isValid = subject.isNotBlank() && daysUntilResult != null && time.length == 5

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormSectionHeader(icon = Icons.Default.Alarm, title = "Thông tin kỳ thi")

        OutlinedTextField(
            value = subject, onValueChange = { subject = it },
            label = { Text("Tên môn thi *") },
            leadingIcon = { Icon(Icons.Default.School, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
            OutlinedTextField(
                value = examType.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hình thức thi") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Assignment, null, Modifier.size(20.dp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp)
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
                value = date, 
                onValueChange = { },
                readOnly = true,
                label = { Text("Ngày thi *") },
                placeholder = { Text(currentDate) },
                isError = !isDateValid,
                modifier = Modifier
                    .weight(1.5f)
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = if (!isDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (!isDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledLabelColor = if (!isDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = time, 
                onValueChange = { },
                readOnly = true,
                label = { Text("Giờ thi") },
                placeholder = { Text("07:30") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    millis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        date = sdf.format(Date(it))
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
        if (showTimePicker) {
            TimePickerModal(
                onTimeSelected = { h: Int, m: Int ->
                    time = "%02d:%02d".format(h, m)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }
        
        val displayDays = when {
            date.isEmpty() -> "Chưa nhập ngày"
            daysUntilResult == null -> "Ngày không tồn tại!"
            daysUntilResult < 0 -> "Kỳ thi đã qua (${-daysUntilResult} ngày)"
            else -> "Còn $daysUntilResult ngày nữa"
        }

        OutlinedTextField(
            value = displayDays,
            onValueChange = {},
            readOnly = true,
            label = { Text("Trạng thái") },
            leadingIcon = { 
                Icon(
                    if (daysUntilResult != null) Icons.Default.HourglassBottom else Icons.Default.ErrorOutline, 
                    null, 
                    tint = if (daysUntilResult != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = if (daysUntilResult == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                focusedTextColor = if (daysUntilResult == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        )

        OutlinedTextField(
            value = room, onValueChange = { room = it },
            label = { Text("Phòng thi") },
            leadingIcon = { Icon(Icons.Default.Room, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = notes, onValueChange = { notes = it },
            label = { Text("Ghi chú") },
            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, null, Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), maxLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        FormSectionHeader(icon = Icons.Default.Palette, title = "Màu sắc")
        ColorPicker(selectedIndex = colorIndex, onSelect = { colorIndex = it })

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (subject.any { !it.isLetterOrDigit() && !it.isWhitespace() && it != '-' && it != '_' }) {
                    onError("Tên môn thi chứa ký tự không hợp lệ!")
                    return@Button
                }

                val finalDate = if (date.length == 5) {
                    "$date/${Calendar.getInstance().get(Calendar.YEAR)}"
                } else date

                onSave(
                    ExamReminder(
                        id = UUID.randomUUID().toString(),
                        subject = subject.trim(),
                        examType = examType,
                        date = finalDate.trim(),
                        time = time.trim(),
                        room = room.trim(),
                        notes = notes.trim(),
                        daysUntil = daysUntilResult ?: 0,
                        colorIndex = colorIndex
                    )
                )
                subject = ""; date = currentDate; time = ""; room = ""; notes = ""
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


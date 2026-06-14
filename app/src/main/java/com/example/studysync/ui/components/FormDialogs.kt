package com.example.studysync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionFormDialog(
    session: ClassSession? = null,
    initialDay: DayOfWeek = DayOfWeek.MONDAY,
    onDismiss: () -> Unit,
    onConfirm: (ClassSession) -> Unit,
    onError: (String) -> Unit
) {
    var subject   by remember { mutableStateOf(session?.subject ?: "") }
    var teacher   by remember { mutableStateOf(session?.teacher ?: "") }
    var room      by remember { mutableStateOf(session?.room ?: "") }
    var startTime by remember { mutableStateOf(session?.startTime ?: "") }
    var endTime   by remember { mutableStateOf(session?.endTime ?: "") }
    var selectedDay by remember { mutableStateOf(session?.dayOfWeek ?: initialDay) }
    var colorIndex  by remember { mutableIntStateOf(session?.colorIndex ?: 0) }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (session == null) "Thêm lịch học" else "Sửa lịch học", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject, onValueChange = { subject = it },
                    label = { Text("Tên môn học *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = teacher, onValueChange = { teacher = it },
                    label = { Text("Giảng viên") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = room, onValueChange = { room = it },
                    label = { Text("Phòng học") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime, onValueChange = { }, readOnly = true,
                        label = { Text("Giờ bắt đầu") },
                        modifier = Modifier.weight(1f).clickable { showStartTimePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    OutlinedTextField(
                        value = endTime, onValueChange = { }, readOnly = true,
                        label = { Text("Giờ kết thúc") },
                        modifier = Modifier.weight(1f).clickable { showEndTimePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (showStartTimePicker) {
                    TimePickerModal(
                        onTimeSelected = { h, m -> 
                            startTime = "%02d:%02d".format(h, m)
                            showStartTimePicker = false
                        },
                        onDismiss = { showStartTimePicker = false }
                    )
                }
                if (showEndTimePicker) {
                    TimePickerModal(
                        onTimeSelected = { h, m -> 
                            endTime = "%02d:%02d".format(h, m)
                            showEndTimePicker = false
                        },
                        onDismiss = { showEndTimePicker = false }
                    )
                }

                ExposedDropdownMenuBox(expanded = dayExpanded, onExpandedChange = { dayExpanded = it }) {
                    OutlinedTextField(
                        value = selectedDay.displayVi, onValueChange = {}, readOnly = true,
                        label = { Text("Thứ trong tuần") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                    onConfirm(
                        ClassSession(
                            id = session?.id ?: UUID.randomUUID().toString(),
                            subject = subject.trim(),
                            teacher = teacher.trim(),
                            room = room.trim(),
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            dayOfWeek = selectedDay,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamFormDialog(
    exam: ExamReminder? = null,
    onDismiss: () -> Unit,
    onConfirm: (ExamReminder) -> Unit,
    onError: (String) -> Unit
) {
    val currentDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    var subject    by remember { mutableStateOf(exam?.subject ?: "") }
    var date       by remember { mutableStateOf(exam?.date ?: currentDate) }
    var time       by remember { mutableStateOf(exam?.time ?: "") }
    var room       by remember { mutableStateOf(exam?.room ?: "") }
    var notes      by remember { mutableStateOf(exam?.notes ?: "") }
    var examType   by remember { mutableStateOf(exam?.examType ?: ExamType.MIDTERM) }
    var colorIndex by remember { mutableIntStateOf(exam?.colorIndex ?: 0) }
    var typeExpanded by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Re-calculate daysUntil for validation if needed, though we primarily care about subject/date/time
    val isValid = subject.isNotBlank() && date.length >= 5 && time.length == 5

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exam == null) "Thêm lịch thi" else "Sửa lịch thi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject, onValueChange = { subject = it },
                    label = { Text("Tên môn thi *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = examType.label, onValueChange = {}, readOnly = true,
                        label = { Text("Hình thức thi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                        value = date, onValueChange = { }, readOnly = true,
                        label = { Text("Ngày thi *") },
                        modifier = Modifier.weight(1.5f).clickable { showDatePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    OutlinedTextField(
                        value = time, onValueChange = { }, readOnly = true,
                        label = { Text("Giờ thi") },
                        modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                        onTimeSelected = { h, m ->
                            time = "%02d:%02d".format(h, m)
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }

                OutlinedTextField(
                    value = room, onValueChange = { room = it },
                    label = { Text("Phòng thi") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(), maxLines = 3
                )

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
                    if (subject.any { !it.isLetterOrDigit() && !it.isWhitespace() && it != '-' && it != '_' }) {
                        onError("Tên môn thi chứa ký tự không hợp lệ!")
                        return@Button
                    }
                    onConfirm(
                        ExamReminder(
                            id = exam?.id ?: UUID.randomUUID().toString(),
                            subject = subject.trim(),
                            examType = examType,
                            date = date.trim(),
                            time = time.trim(),
                            room = room.trim(),
                            notes = notes.trim(),
                            daysUntil = 0, // This is updated in NavGraph or on display
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
}

package com.example.studysync.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studysync.util.NotificationHelper

class ExamReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val subject = intent.getStringExtra("subject") ?: "Môn học"
        val type = intent.getStringExtra("type") ?: "Kỳ thi"
        val time = intent.getStringExtra("time") ?: ""

        val notificationHelper = NotificationHelper(context)
        notificationHelper.showExamNotification(
            title = "Nhắc nhở lịch thi: $subject",
            message = "Bạn có lịch thi $type vào lúc $time hôm nay. Chúc bạn thi tốt! 🍀"
        )
    }
}

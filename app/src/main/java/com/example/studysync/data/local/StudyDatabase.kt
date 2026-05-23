package com.example.studysync.data.local

import android.content.Context
import androidx.room.*
import com.example.studysync.data.*

class Converters {
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek) = value.name

    @TypeConverter
    fun toDayOfWeek(value: String) = DayOfWeek.valueOf(value)

    @TypeConverter
    fun fromExamType(value: ExamType) = value.name

    @TypeConverter
    fun toExamType(value: String) = ExamType.valueOf(value)
}

@Database(entities = [ClassSession::class, ExamReminder::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: StudyDatabase? = null

        fun getDatabase(context: Context): StudyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "study_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

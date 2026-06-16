package com.example.studysync.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.studysync.data.local.StudyDatabase
import com.example.studysync.data.repository.OfflineStudyRepository
import com.example.studysync.data.repository.StudyRepository
import com.example.studysync.data.repository.UserPreferencesRepository

private const val THEME_PREFERENCE_NAME = "theme_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCE_NAME
)

interface AppContainer {
    val studyRepository: StudyRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val studyRepository: StudyRepository by lazy {
        OfflineStudyRepository(StudyDatabase.getDatabase(context).studyDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}

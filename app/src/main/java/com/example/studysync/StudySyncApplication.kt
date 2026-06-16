package com.example.studysync

import android.app.Application
import com.example.studysync.data.AppContainer
import com.example.studysync.data.AppDataContainer

class StudySyncApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

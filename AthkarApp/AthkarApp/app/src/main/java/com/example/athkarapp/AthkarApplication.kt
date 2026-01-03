package com.example.athkarapp

import android.app.Application
import com.example.athkarapp.data.AzkarDatabase
import com.example.athkarapp.data.AzkarRepository
import com.example.athkarapp.data.HapticManager
import com.example.athkarapp.data.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AthkarApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AzkarDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AzkarRepository(database.azkarDao()) }
    val settingsManager by lazy { SettingsManager(this) }
    val hapticManager by lazy { HapticManager(this) }
}

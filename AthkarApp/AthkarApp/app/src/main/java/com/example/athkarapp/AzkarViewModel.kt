package com.example.athkarapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.athkarapp.data.AzkarRepository
import com.example.athkarapp.data.SettingsManager
import com.example.athkarapp.data.UserProgressEntity
import com.example.athkarapp.data.ZikrEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AzkarViewModel(
    private val repository: AzkarRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            _isDarkTheme.value = settingsManager.isDarkTheme.first()
            repository.populateInitialData()
        }
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsManager.setTheme(isDark)
            _isDarkTheme.value = isDark
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            repository.resetToDefault()
        }
    }

    fun getAzkarByCategory(category: String): Flow<List<ZikrEntity>> {
        return repository.getAzkarByCategory(category)
    }

    fun insert(zikr: ZikrEntity) = viewModelScope.launch {
        repository.insert(zikr)
    }

    fun update(zikr: ZikrEntity) = viewModelScope.launch {
        repository.update(zikr)
    }

    fun delete(zikr: ZikrEntity) = viewModelScope.launch {
        repository.delete(zikr)
    }

    fun updateOrder(azkar: List<ZikrEntity>) = viewModelScope.launch {
        repository.updateOrder(azkar)
    }

    val userProgress: Flow<UserProgressEntity?> = repository.getUserProgress()

    fun incrementTotalReads(count: Int) = viewModelScope.launch {
        repository.incrementTotalReads(count)
    }
}

class AzkarViewModelFactory(
    private val repository: AzkarRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AzkarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AzkarViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.athkarapp.data

import kotlinx.coroutines.flow.Flow

class AzkarRepository(private val azkarDao: AzkarDao) {

    suspend fun populateInitialData() {
        if (azkarDao.getAzkarCount() == 0) {
            // Populate Morning Azkar
            val morningEntities = AzkarData.morningAzkar.mapIndexed { index, item ->
                ZikrEntity(
                    text = item.text,
                    count = item.count,
                    benefit = item.benefit,
                    category = "MORNING",
                    orderIndex = index
                )
            }
            azkarDao.insertAll(morningEntities)

            // Populate Evening Azkar
            val eveningEntities = AzkarData.eveningAzkar.mapIndexed { index, item ->
                ZikrEntity(
                    text = item.text,
                    count = item.count,
                    benefit = item.benefit,
                    category = "EVENING",
                    orderIndex = index
                )
            }
            azkarDao.insertAll(eveningEntities)

            // Initialize progress
            azkarDao.insertOrUpdateProgress(UserProgressEntity(totalAzkarRead = 0))
        }
    }

    suspend fun resetToDefault() {
        azkarDao.clearAzkar()
        populateInitialData()
    }

    fun getAzkarByCategory(category: String): Flow<List<ZikrEntity>> {
        return azkarDao.getAzkarByCategory(category)
    }

    suspend fun insert(zikr: ZikrEntity) {
        azkarDao.insert(zikr)
    }

    suspend fun update(zikr: ZikrEntity) {
        azkarDao.update(zikr)
    }

    suspend fun delete(zikr: ZikrEntity) {
        azkarDao.delete(zikr)
    }

    suspend fun updateOrder(azkar: List<ZikrEntity>) {
        azkarDao.updateOrder(azkar)
    }

    fun getUserProgress(): Flow<UserProgressEntity?> {
        return azkarDao.getUserProgress()
    }

    suspend fun incrementTotalReads(count: Int) {
        azkarDao.incrementTotalReads(count)
    }
}

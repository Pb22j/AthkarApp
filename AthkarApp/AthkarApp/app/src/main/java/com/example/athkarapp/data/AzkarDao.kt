package com.example.athkarapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {
    @Query("SELECT * FROM azkar_table WHERE category = :category ORDER BY orderIndex ASC")
    fun getAzkarByCategory(category: String): Flow<List<ZikrEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(zikr: ZikrEntity)

    @Update
    suspend fun update(zikr: ZikrEntity)

    @Delete
    suspend fun delete(zikr: ZikrEntity)

    @Query("SELECT COUNT(*) FROM azkar_table")
    suspend fun getAzkarCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(azkar: List<ZikrEntity>)

    @Query("DELETE FROM azkar_table")
    suspend fun clearAzkar()

    @Transaction
    suspend fun updateOrder(azkar: List<ZikrEntity>) {
        azkar.forEach { update(it) }
    }

    // User Progress
    @Query("SELECT * FROM user_progress_table WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress_table SET totalAzkarRead = totalAzkarRead + :count WHERE id = 1")
    suspend fun incrementTotalReads(count: Int)
}

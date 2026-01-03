package com.example.athkarapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkar_table")
data class ZikrEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val count: Int,
    val benefit: String = "",
    val category: String, // "MORNING" or "EVENING"
    val orderIndex: Int
)

@Entity(tableName = "user_progress_table")
data class UserProgressEntity(
    @PrimaryKey
    val id: Int = 1, // Only one row for total count
    val totalAzkarRead: Long = 0
)

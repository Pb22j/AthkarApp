package com.example.athkarapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [ZikrEntity::class, UserProgressEntity::class], version = 2, exportSchema = false)
abstract class AzkarDatabase : RoomDatabase() {
    abstract fun azkarDao(): AzkarDao

    companion object {
        @Volatile
        private var Instance: AzkarDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AzkarDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AzkarDatabase::class.java,
                    "azkar_database"
                )
                .fallbackToDestructiveMigration() // Simplified migration strategy
                .build()
                Instance = instance
                instance
            }
        }
    }
}

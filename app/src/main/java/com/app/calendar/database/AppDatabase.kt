package com.app.calendar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.calendar.converter.LocalDateConverter
import com.app.calendar.dao.TrainingDao
import com.app.calendar.model.TrainingEntity

@Database(entities = [TrainingEntity::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {

        private var db:AppDatabase? = null

        fun createDatabase(applicationContext: Context): AppDatabase {
            return db ?: synchronized(this) {
                val instance = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()
                db = instance
                instance
            }
        }
    }
    abstract fun trainingDao(): TrainingDao
}
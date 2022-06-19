package com.app.body_manage.data.dao.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.body_manage.data.dao.BodyMeasureDao
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.PhotoEntity

@Database(
    entities = [BodyMeasureEntity::class, PhotoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private var db: AppDatabase? = null

        fun createDatabase(applicationContext: Context): AppDatabase {
            return db ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database")
                        .build()
                db = instance
                instance
            }
        }
    }

    abstract fun bodyMeasureDao(): BodyMeasureDao
    abstract fun photoDao(): PhotoDao
    abstract fun bodyMeasurePhotoDao(): BodyMeasurePhotoDao
}
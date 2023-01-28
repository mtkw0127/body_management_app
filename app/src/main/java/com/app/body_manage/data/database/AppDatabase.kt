package com.app.body_manage.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.body_manage.data.dao.BodyMeasureDao
import com.app.body_manage.data.dao.BodyMeasurePhotoDao
import com.app.body_manage.data.dao.CompareBodyMeasureHistoryDao
import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.CompareBodyMeasureHistoryEntity
import com.app.body_manage.data.entity.PhotoEntity

@Database(
    entities = [BodyMeasureEntity::class, PhotoEntity::class, CompareBodyMeasureHistoryEntity::class],
    version = 2,
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
                        .apply {
                            allowMainThreadQueries()
                            addMigrations(MIGRATION_1_2)
                        }.build()
                db = instance
                instance
            }
        }
    }

    abstract fun bodyMeasureDao(): BodyMeasureDao
    abstract fun photoDao(): PhotoDao
    abstract fun bodyMeasurePhotoDao(): BodyMeasurePhotoDao
    abstract fun compareBodyMeasureHistoryDao(): CompareBodyMeasureHistoryDao
}

// 体重カラムをBodyMeasureTableに追加
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE bodyMeasures ADD COLUMN tall FLOAT"
        )
    }
}

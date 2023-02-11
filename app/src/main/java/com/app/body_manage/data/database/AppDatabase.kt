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
import com.app.body_manage.data.dao.ComparePhotoHistoryDao
import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import com.app.body_manage.data.entity.PhotoEntity

@Database(
    entities = [BodyMeasureEntity::class, PhotoEntity::class, ComparePhotoHistoryEntity::class],
    version = 4,
    exportSchema = true
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
                            addMigrations(MIGRATION_2_3)
                            addMigrations(MIGRATION_3_4)
                        }.build()
                db = instance
                instance
            }
        }
    }

    abstract fun bodyMeasureDao(): BodyMeasureDao
    abstract fun photoDao(): PhotoDao
    abstract fun bodyMeasurePhotoDao(): BodyMeasurePhotoDao
    abstract fun compareBodyMeasureHistoryDao(): ComparePhotoHistoryDao
}

/** 体重カラムをBodyMeasureTableに追加*/
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE bodyMeasures ADD COLUMN tall FLOAT"
        )
    }
}

/** 比較履歴のテーブルを追加*/
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `compareBodyMeasureHistory` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `beforeBodyMeasureId` INTEGER NOT NULL, `afterBodyMeasureId` INTEGER NOT NULL)",
        )
    }
}

/** 比較履歴のテーブルを変更*/
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `comparePhotoHistory` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `beforePhotoId` INTEGER NOT NULL, `afterPhotoId` INTEGER NOT NULL, `createdAt` TEXT NOT NULL)"
        )
        database.execSQL(
            "DROP TABLE `compareBodyMeasureHistory`"
        )
    }
}

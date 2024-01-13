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
import com.app.body_manage.data.dao.MealFoodsDao
import com.app.body_manage.data.dao.PhotoDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import com.app.body_manage.data.entity.FoodEntity
import com.app.body_manage.data.entity.MealEntity
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.MealPhotoEntity
import com.app.body_manage.data.entity.PhotoEntity

@Database(
    entities = [
        BodyMeasureEntity::class,
        PhotoEntity::class,
        ComparePhotoHistoryEntity::class,
        MealEntity::class,
        FoodEntity::class,
        MealFoodCrossRef::class,
        MealPhotoEntity::class,
    ],
    version = 7,
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
                            addMigrations(MIGRATION_4_5)
                            addMigrations(MIGRATION_5_6)
                            addMigrations(MIGRATION_6_7)
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
    abstract fun mealFoodsDao(): MealFoodsDao
}

/** 体重カラムをBodyMeasureTableに追加*/
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE bodyMeasures ADD COLUMN tall FLOAT"
        )
    }
}

/** 比較履歴のテーブルを追加*/
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `compareBodyMeasureHistory` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `beforeBodyMeasureId` INTEGER NOT NULL, `afterBodyMeasureId` INTEGER NOT NULL)",
        )
    }
}

/** 比較履歴のテーブルを変更*/
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `comparePhotoHistory` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `beforePhotoId` INTEGER NOT NULL, `afterPhotoId` INTEGER NOT NULL, `createdAt` TEXT NOT NULL)"
        )
        db.execSQL(
            "DROP TABLE `compareBodyMeasureHistory`"
        )
    }
}

/** メモカラムの追加*/
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE bodyMeasures ADD COLUMN memo TEXT DEFAULT \"\" NOT NULL "
        )
    }
}

/** 食事テーブルの追加・食事写真テーブルの追加*/
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `meals` (`meal_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timing` TEXT NOT NULL, `dateTime` TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `foods` (`food_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `name_jp` TEXT NOT NULL, `name_kana` TEXT NOT NULL, `kcal` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `meal_photos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `meal_id` INTEGER NOT NULL, `photo_uri` TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `mealAndFood` (`meal_id` INTEGER NOT NULL, `food_id` INTEGER NOT NULL)"
        )
    }
}

/** mealAndFoodテーブルに個数を追加（りんごを２つ食べたみたいな登録ができるようにするため）*/
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `mealAndFood` ADD COLUMN `number` INTEGER DEFAULT 1 NOT NULL"
        )
    }
}

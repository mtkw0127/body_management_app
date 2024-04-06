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
import com.app.body_manage.data.dao.TrainingDao
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.entity.ComparePhotoHistoryEntity
import com.app.body_manage.data.entity.FoodEntity
import com.app.body_manage.data.entity.MealEntity
import com.app.body_manage.data.entity.MealFoodCrossRef
import com.app.body_manage.data.entity.MealPhotoEntity
import com.app.body_manage.data.entity.PhotoEntity
import com.app.body_manage.data.entity.TrainingEntity
import com.app.body_manage.data.entity.TrainingMenuEntity
import com.app.body_manage.data.entity.TrainingSetEntity

@Database(
    entities = [
        BodyMeasureEntity::class,
        PhotoEntity::class,
        ComparePhotoHistoryEntity::class,
        MealEntity::class,
        FoodEntity::class,
        MealFoodCrossRef::class,
        MealPhotoEntity::class,
        TrainingEntity::class,
        TrainingMenuEntity::class,
        TrainingSetEntity::class,
    ],
    version = 9,
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
                            addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                                MIGRATION_4_5,
                                MIGRATION_6_7,
                                MIGRATION_7_8,
                                MIGRATION_8_9,
                            )
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
    abstract fun trainingDao(): TrainingDao
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

/**
 * トレーニングテーブルの追加
 * トレーニングメニューテーブルの追加
 * トレーニングセットの追加
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `trainings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `start_time` TEXT NOT NULL, `end_time` TEXT NOT NULL, `memo` TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `training_menus` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `part` INTEGER NOT NULL, `memo` TEXT NOT NULL, `type` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `training_sets`  (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `training_id` INTEGER NOT NULL, `training_menu_id` INTEGER NOT NULL, `rep` INTEGER NOT NULL, `weight` INTEGER NOT NULL)"
        )
    }
}

/**
 * 筋トレメニューの初期データを追加
 */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 胸
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ベンチプレス', 10, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ダンベルベンチプレス', 10, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ダンベルフライ', 10, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('チェストプレス', 10, '', 1)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('腕立て伏せ', 10, '', 3)"
        )
        // 腕
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ダンベルアームカール', 20, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('アームカール', 20, '', 1)"
        )
        // 肩
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ショルダープレス', 20, '', 1)"
        )
        // 背中
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('デッドリフト', 30, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('ラッドプルダウン', 30, '', 1)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('懸垂', 30, '', 3)"
        )
        // 腹
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('腹筋', 50, '', 3)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('アブドミナル', 70, '', 1)"
        )
        // 脚
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('スクワット', 70, '', 2)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('レッグプレス', 70, '', 1)"
        )
        db.execSQL(
            "INSERT INTO training_menus (name, part, memo, type) VALUES ('自重スクワット', 70, '', 3)"
        )
    }
}

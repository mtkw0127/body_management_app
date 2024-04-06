package com.app.body_manage

import android.app.Application
import com.app.body_manage.data.database.AppDatabase
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository
import com.app.body_manage.data.repository.BodyMeasureRepository
import com.app.body_manage.data.repository.CompareHistoryRepository
import com.app.body_manage.data.repository.MealRepository
import com.app.body_manage.data.repository.PhotoRepository
import com.app.body_manage.data.repository.TrainingRepository
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader

class TrainingApplication : Application() {
    private val database by lazy { AppDatabase.createDatabase(this) }
    val bodyMeasureRepository by lazy { BodyMeasureRepository(database.bodyMeasureDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao()) }
    val bodyMeasurePhotoRepository by lazy { BodyMeasurePhotoRepository(database.bodyMeasurePhotoDao()) }
    val compareBodyMeasureHistoryRepository by lazy {
        CompareHistoryRepository(
            database.compareBodyMeasureHistoryDao()
        )
    }
    val mealFoodsRepository by lazy { MealRepository(database.mealFoodsDao()) }
    val trainingRepository by lazy { TrainingRepository(database.trainingDao()) }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.start()
        }
    }
}

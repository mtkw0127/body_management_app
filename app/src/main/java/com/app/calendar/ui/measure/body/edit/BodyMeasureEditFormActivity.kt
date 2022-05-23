package com.app.calendar.ui.measure.body.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.app.calendar.R.id
import com.app.calendar.R.layout
import com.app.calendar.TrainingApplication
import com.app.calendar.dialog.FloatNumberPickerDialog
import com.app.calendar.dialog.TimePickerDialog
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.ui.camera.CameraActivity
import com.app.calendar.util.DateUtil
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BodyMeasureEditFormActivity : AppCompatActivity() {

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).repository
    }

    private lateinit var localDateTime: LocalDateTime
    private lateinit var measureTimeView: TextView
    private lateinit var weightField: TextView
    private lateinit var fatField: TextView

    private var photoUri: Uri? = null
    private var measureTime: LocalDateTime = LocalDateTime.now()
    private var measureWeight = 50F
    private var measureFat = 20.0F

    // coroutineによるローディング取得
    private var loadingEntity = true

    private lateinit var bodyMeasureEntity: BodyMeasureEntity

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val activityResult = it.data?.extras?.get(CameraActivity.INTENT_KEY_PHOTO_URI)
            if (activityResult != null) {
                photoUri = activityResult as Uri
                findViewById<ImageView>(id.prev_img).setImageURI(photoUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.training_detail)

        measureTimeView = findViewById(id.training_time)
        weightField = findViewById(id.weight)
        fatField = findViewById(id.fat)

        localDateTime = intent.getSerializableExtra(INTENT_KEY) as LocalDateTime

        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.Main).launch {
            findViewById<TextView>(id.date_text).text = localDateTime.toLocalDate().toString()
            try {
                val flow = bodyMeasureRepository.getEntityByCaptureTime(localDateTime)
                flow.collect {
                    bodyMeasureEntity = it
                    measureTimeView.text =
                        DateUtil.localDateConvertLocalTimeDateToTime(it.capturedTime)
                    weightField.text = "${it.weight}kg"
                    fatField.text = "${it.fatRate}%"

                    measureTime = it.capturedTime
                    measureWeight = it.weight
                    measureFat = it.fatRate
                    photoUri = it.photoUri?.toUri()

                    findViewById<ImageView>(id.prev_img).setImageURI(it.photoUri?.toUri())
                    // ロード中終了
                    this@BodyMeasureEditFormActivity.loadingEntity = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // カメラフィールド
        val cameraStartButton = findViewById<ImageView>(id.prev_img)
        cameraStartButton.setOnClickListener {
            val intent = CameraActivity.createCameraActivityIntent(applicationContext)
            cameraActivityLauncher.launch(intent)
        }

        // 戻るボタン
        val backBtn = findViewById<Button>(id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

        // 計測時刻
        measureTimeView.setOnClickListener {
            val timePickerFragment = TimePickerDialog.createTimePickerDialog(
                measureTime.hour,
                measureTime.minute
            ) { hour, minute ->
                val hourStr = String.format("%02d", hour)
                val minuteStr = String.format("%02d", minute)
                val time = "${hourStr}時${minuteStr}分"
                (it as TextView).text = time
                // 計測時刻更新
                measureTime = LocalDateTime.of(
                    measureTime.year,
                    measureTime.monthValue,
                    measureTime.dayOfMonth,
                    hour,
                    minute
                )
            }
            timePickerFragment.show(supportFragmentManager, "TimePicker")
        }

        // 体重
        weightField.setOnClickListener {
            val weightPickerFragment =
                FloatNumberPickerDialog.createDialog(measureWeight, "kg") { weight ->
                    (it as TextView).text = "${weight}kg"
                    measureWeight = weight
                }
            weightPickerFragment.show(supportFragmentManager, "WeightPicker")
        }

        // 体脂肪率
        fatField.setOnClickListener {
            val fatPickerFragment =
                FloatNumberPickerDialog.createDialog(measureFat, "%") { fat ->
                    (it as TextView).text = "${fat}kg"
                    measureFat = fat
                }
            fatPickerFragment.show(supportFragmentManager, "FatPicker")
        }

        // 保存ボタン
        val saveBtn = findViewById<Button>(id.save_btn)
        saveBtn.setOnClickListener {
            if (this.loadingEntity.not()) {
                val saveModel = BodyMeasureEntity(
                    bodyMeasureEntity.ui,
                    localDateTime.toLocalDate(),// カレンダー日付
                    localDateTime.toLocalDate(),// キャプチャ日付
                    measureTime,
                    measureWeight,
                    measureFat,
                    photoUri?.path
                )
                CoroutineScope(Dispatchers.Main).launch {
                    bodyMeasureRepository.update(saveModel)
                }
                finish()
            }
        }
    }

    companion object {
        const val INTENT_KEY = "CAPTURE_DATE_TIME"
        fun createMeasureFormEditIntent(
            context: Context,
            captureTime: LocalDateTime
        ): Intent {
            val intent = Intent(context.applicationContext, BodyMeasureEditFormActivity::class.java)
            intent.putExtra(INTENT_KEY, captureTime)
            return intent
        }
    }
}
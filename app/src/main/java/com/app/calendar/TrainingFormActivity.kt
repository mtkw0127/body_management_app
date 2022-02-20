package com.app.calendar

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
import com.app.calendar.dialog.TimePickerDialog
import com.app.calendar.dialog.FloatNumberPickerDialog
import com.app.calendar.model.TrainingEntity
import com.app.calendar.repository.TrainingRepository
import com.app.calendar.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

class TrainingFormActivity: AppCompatActivity() {

    private val trainingRepository: TrainingRepository by lazy {
        (application as TrainingApplication).repository
    }

    companion object {
        const val INTENT_KEY = "DATE"
        const val INTENT_RESULT_KEY = "INTENT_RESULT_KEY"
        fun createTrainingMeasureFormIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context.applicationContext, TrainingFormActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }

    private lateinit var localDate: LocalDate
    private lateinit var measureTimeView: TextView
    private lateinit var weightField: TextView
    private lateinit var fatField: TextView


    private var photoUri: Uri? = null
    private var measureTime: LocalDateTime = LocalDateTime.now()
    private var measureWeight = 50F
    private var measureFat = 20.0F

    // coroutineによるローディング取得
    private var loadingEntity = true

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val activityResult = it.data?.extras?.get(CameraActivity.INTENT_KEY_PHOTO_URI)
            if(activityResult != null) {
                photoUri = activityResult as Uri
                findViewById<ImageView>(R.id.prev_img).setImageURI(photoUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_detail)

        measureTimeView = findViewById(R.id.training_time)
        weightField = findViewById(R.id.weight)
        fatField = findViewById(R.id.fat)

        localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        // 当該日のLocalDateTime.nowを取得
        measureTime = LocalDateTime.now()
            .withYear(localDate.year)
            .withMonth(localDate.monthValue)
            .withDayOfYear(localDate.dayOfMonth)

        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.Main).launch {
            findViewById<TextView>(R.id.date_text).text = localDate.toString()
            val trainingEntityList = trainingRepository.getEntityListByDate(localDate)

            // ロード中終了
            loadingEntity = false
        }

        // カメラフィールド
        val cameraStartButton = findViewById<ImageView>(R.id.prev_img)
        cameraStartButton.setOnClickListener {
            val intent = CameraActivity.createCameraActivityIntent(applicationContext)
            cameraActivityLauncher.launch(intent)
        }

        // 戻るボタン
        val backBtn = findViewById<Button>(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

        // 計測時刻初期値
        measureTimeView.text = DateUtil.localDateConvertLocalTimeDateToTime(LocalDateTime.now())

        // 計測時刻
        measureTimeView.setOnClickListener {
            val timePickerFragment = TimePickerDialog.createTimePickerDialog(measureTime.hour,measureTime.minute) {hour, minute ->
                val hourStr = String.format("%02d", hour)
                val minuteStr =String.format("%02d", minute)
                val time = "${hourStr}時${minuteStr}分"
                (it as TextView).text = time
                // 計測時刻更新
                measureTime = LocalDateTime.of(measureTime.year,measureTime.monthValue,measureTime.dayOfMonth,hour,minute)
            }
            timePickerFragment.show(supportFragmentManager, "TimePicker")
        }

        // 体重
        weightField.setOnClickListener {
            val weightPickerFragment = FloatNumberPickerDialog.createDialog(measureWeight, "kg") { weight ->
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
        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            if(!loadingEntity) {
                val saveModel = TrainingEntity(
                    0,
                    localDate,// カレンダー日付
                    localDate,// キャプチャ日付
                    measureTime,
                    measureWeight,
                    measureFat,
                    photoUri?.path
                )
                CoroutineScope(Dispatchers.Main).launch {
                    trainingRepository.insert(saveModel)
                }

                intent.putExtra(INTENT_RESULT_KEY, saveModel)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
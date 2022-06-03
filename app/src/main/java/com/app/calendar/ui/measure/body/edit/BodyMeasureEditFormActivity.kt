package com.app.calendar.ui.measure.body.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.app.calendar.TrainingApplication
import com.app.calendar.databinding.TrainingDetailBinding
import com.app.calendar.dialog.FloatNumberPickerDialog
import com.app.calendar.dialog.TimePickerDialog
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.repository.BodyMeasureRepository
import com.app.calendar.ui.camera.CameraActivity
import com.app.calendar.util.DateUtil
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BodyMeasureEditFormActivity : AppCompatActivity() {

    enum class FormType {
        ADD, EDIT
    }

    private val bodyMeasureRepository: BodyMeasureRepository by lazy {
        (application as TrainingApplication).repository
    }

    private lateinit var binding: TrainingDetailBinding

    private val captureDate: LocalDate by lazy { intent.getSerializableExtra(KEY_CAPTURE_DATE) as LocalDate }
    private val localDateTime: LocalDateTime by lazy { intent.getSerializableExtra(KEY_CAPTURED_TIME) as LocalDateTime }

    private var photoUri: Uri? = null
    private var measureTime: LocalDateTime = LocalDateTime.now()
    private var measureWeight = 50F
    private var measureFat = 20.0F

    // coroutineによるローディング取得
    private var loadingEntity = false

    private lateinit var bodyMeasureEntity: BodyMeasureEntity

    private val formType: FormType by lazy { intent.getSerializableExtra(FORM_TYPE) as FormType }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val activityResult = it.data?.extras?.get(CameraActivity.INTENT_KEY_PHOTO_URI)
            if (activityResult != null) {
                photoUri = activityResult as Uri
                binding.prevImg.setImageURI(photoUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrainingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dateText.text = captureDate.toString()
        setListener()
        when (formType) {
            FormType.ADD -> {}
            FormType.EDIT -> {
                loadBodyMeasure()
            }
        }
    }

    private fun loadBodyMeasure() {
        loadingEntity = true
        // 対象の日付に紐づくデータが存在すれば取得する.
        CoroutineScope(Dispatchers.Main).launch {
            try {
                runCatching { bodyMeasureRepository.getEntityByCaptureTime(localDateTime) }
                    .onFailure { e -> e.printStackTrace() }
                    .onSuccess { res ->
                        val it = res[0]
                        bodyMeasureEntity = it
                        binding.trainingTime.text =
                            DateUtil.localDateConvertLocalTimeDateToTime(it.capturedTime)
                        binding.weight.text = "${it.weight}kg"
                        binding.fat.text = "${it.fatRate}%"
                        measureTime = it.capturedTime
                        measureWeight = it.weight
                        measureFat = it.fatRate
                        photoUri = it.photoUri?.toUri()

                        binding.prevImg.setImageURI(it.photoUri?.toUri())
                    }.also {
                        // ロード中終了
                        this@BodyMeasureEditFormActivity.loadingEntity = false
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setListener() {
        // カメラフィールド
        binding.prevImg.setOnClickListener {
            val intent = CameraActivity.createCameraActivityIntent(applicationContext)
            cameraActivityLauncher.launch(intent)
        }

        // 戻るボタン
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 計測時刻
        binding.trainingTime.setOnClickListener {
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
        binding.weight.setOnClickListener {
            val weightPickerFragment =
                FloatNumberPickerDialog.createDialog(measureWeight, "kg") { weight ->
                    (it as TextView).text = "${weight}kg"
                    measureWeight = weight
                }
            weightPickerFragment.show(supportFragmentManager, "WeightPicker")
        }

        // 体脂肪率
        binding.fat.setOnClickListener {
            val fatPickerFragment =
                FloatNumberPickerDialog.createDialog(measureFat, "%") { fat ->
                    (it as TextView).text = "${fat}kg"
                    measureFat = fat
                }
            fatPickerFragment.show(supportFragmentManager, "FatPicker")
        }

        // 保存ボタン
        binding.saveBtn.setOnClickListener {
            if (this.loadingEntity.not()) {
                val saveModel = BodyMeasureEntity(
                    0,
                    captureDate,// カレンダー日付
                    captureDate,// キャプチャ日付
                    measureTime,
                    measureWeight,
                    measureFat,
                    photoUri?.path
                )
                CoroutineScope(Dispatchers.IO).launch {
                    when (formType) {
                        FormType.ADD -> {
                            bodyMeasureRepository.insert(saveModel)
                        }
                        FormType.EDIT -> {
                            saveModel.ui = bodyMeasureEntity.ui
                            bodyMeasureRepository.update(saveModel)
                        }
                    }
                }
                finish()
            }
        }
    }

    companion object {
        private const val KEY_CAPTURE_DATE = "CAPTURE_DATE_TIME"
        private const val KEY_CAPTURED_TIME = "CAPTURED_TIME"
        private const val FORM_TYPE = "FORM_TYPE"
        fun createMeasureEditIntent(
            context: Context,
            formType: FormType = FormType.EDIT,
            captureTime: LocalDateTime,
        ): Intent {
            val intent =
                Intent(context.applicationContext, BodyMeasureEditFormActivity::class.java)
            intent.putExtra(FORM_TYPE, formType)
            intent.putExtra(KEY_CAPTURE_DATE, captureTime.toLocalDate())
            intent.putExtra(KEY_CAPTURED_TIME, captureTime)
            return intent
        }

        fun createMeasureFormIntent(
            context: Context,
            formType: FormType = FormType.ADD,
            formDate: LocalDate,
        ): Intent {
            val intent =
                Intent(context.applicationContext, BodyMeasureEditFormActivity::class.java)
            intent.putExtra(FORM_TYPE, formType)
            intent.putExtra(KEY_CAPTURE_DATE, formDate)
            return intent
        }
    }
}
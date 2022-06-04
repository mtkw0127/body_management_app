package com.app.calendar.ui.measure.body.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.app.calendar.databinding.TrainingDetailBinding
import com.app.calendar.dialog.FloatNumberPickerDialog
import com.app.calendar.dialog.TimePickerDialog
import com.app.calendar.model.BodyMeasureEntity
import com.app.calendar.ui.camera.CameraActivity
import com.app.calendar.util.DateUtil
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs

class BodyMeasureEditFormActivity : AppCompatActivity() {

    enum class FormType {
        ADD, EDIT
    }

    private lateinit var binding: TrainingDetailBinding

    private val formType: FormType by lazy {
        intent.getSerializableExtra(FORM_TYPE) as FormType
    }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val photoList = CameraActivity.photoList.toList()
            vm.photoList.value?.addAll(photoList)
            vm.photoList.value = vm.photoList.value
        }
    }

    private var vm = BodyMeasureEditFormViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrainingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // カメラ内部のキャッシュをクリア
        CameraActivity.photoList.clear()
        // ViewModelにapplication設定
        vm.application = application
        vm.intent = intent
        // 日付設定
        binding.dateText.text = DateUtil.localDateConvertJapaneseFormatYearMonthDay(vm.captureDate)

        setListener()
        initPagerAdapter()
        when (formType) {
            FormType.ADD -> {
                // 新規追加の場合は、今の時刻を設定
                binding.trainingTime.text =
                    DateUtil.localDateConvertLocalTimeDateToTime(LocalDateTime.now())
            }
            FormType.EDIT -> {
                // 紐づく測定結果、写真を取得してフィールドに設定
                vm.loadBodyMeasure()
            }
        }
    }

    private fun initPagerAdapter() {
        binding.prevImg.adapter = SliderAdapter(listOf())
        binding.prevImg.clipToPadding = false
        binding.prevImg.clipChildren = false
        binding.prevImg.offscreenPageLimit = 3
        binding.prevImg.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.prevImg.setPageTransformer(compositePageTransformer)
    }

    private fun setListener() {
        // 測定結果を画面描画し、写真をロード
        vm.loadedBodyMeasure.observe(this) {
            if (it) {
                binding.trainingTime.text =
                    DateUtil.localDateConvertLocalTimeDateToTime(vm.bodyMeasureEntity.capturedTime)
                binding.weight.text = "${vm.bodyMeasureEntity.weight}kg"
                binding.fat.text = "${vm.bodyMeasureEntity.fatRate}%"

                // 紐づく写真を取得
                vm.loadPhotos()
            }
        }
        // 写真のローディング完了
        vm.loadedPhoto.observe(this) {
            if (it) {
                binding.prevImg.adapter = SliderAdapter(checkNotNull(vm.photoList.value).toList())
            }
        }

        // カメラフィールド
        binding.camera.setOnClickListener {
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
                vm.measureTime.hour,
                vm.measureTime.minute
            ) { hour, minute ->
                val hourStr = String.format("%02d", hour)
                val minuteStr = String.format("%02d", minute)
                val time = "${hourStr}時${minuteStr}分"
                (it as TextView).text = time
                // 計測時刻更新
                vm.measureTime = LocalDateTime.of(
                    vm.measureTime.year,
                    vm.measureTime.monthValue,
                    vm.measureTime.dayOfMonth,
                    hour,
                    minute
                )
            }
            timePickerFragment.show(supportFragmentManager, "TimePicker")
        }

        // 体重
        binding.weight.setOnClickListener {
            val weightPickerFragment =
                FloatNumberPickerDialog.createDialog(vm.measureWeight, "kg") { weight ->
                    (it as TextView).text = "${weight}kg"
                    vm.measureWeight = weight
                }
            weightPickerFragment.show(supportFragmentManager, "WeightPicker")
        }

        // 体脂肪率
        binding.fat.setOnClickListener {
            val fatPickerFragment =
                FloatNumberPickerDialog.createDialog(vm.measureFat, "%") { fat ->
                    (it as TextView).text = "${fat}kg"
                    vm.measureFat = fat
                }
            fatPickerFragment.show(supportFragmentManager, "FatPicker")
        }

        // 保存ボタン
        binding.saveBtn.setOnClickListener {
            val saveModel = BodyMeasureEntity(
                0,
                vm.captureDate,// カレンダー日付
                vm.captureDate,// キャプチャ日付
                vm.measureTime,
                vm.measureWeight,
                vm.measureFat,
                null
            )
            when (formType) {
                FormType.ADD -> {
                    vm.addPhoto(saveModel)
                }
                FormType.EDIT -> {
                    vm.editPhoto(saveModel)
                }
            }
            finish()
        }

        vm.photoList.observe(this) {
            binding.prevImg.adapter = SliderAdapter(it.toList())
        }
    }


    companion object {
        const val KEY_CAPTURE_DATE = "CAPTURE_DATE_TIME"
        const val KEY_CAPTURED_TIME = "CAPTURED_TIME"
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
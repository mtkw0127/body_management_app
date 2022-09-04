package com.app.body_manage.ui.measure.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.databinding.TrainingDetailBinding
import com.app.body_manage.dialog.FloatNumberPickerDialog
import com.app.body_manage.dialog.TimePickerDialog
import com.app.body_manage.ui.camera.CameraActivity
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel.PhotoModel
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel.PhotoType.ADDED
import com.app.body_manage.util.DateUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs

class BodyMeasureEditFormActivity : AppCompatActivity() {

    enum class FormType {
        ADD, EDIT
    }

    private lateinit var binding: TrainingDetailBinding

    // 更新前の測定日時
    private val captureDateTime: LocalDateTime by lazy {
        intent.getSerializableExtra(
            KEY_CAPTURE_TIME
        ) as LocalDateTime
    }

    private val formType: FormType by lazy {
        intent.getSerializableExtra(FORM_TYPE) as FormType
    }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val photoList = CameraActivity.photoList.toList()
            val photoModels =
                photoList.map { _uri -> PhotoModel(uri = _uri, photoType = ADDED) }.toList()
            vm.addPhotos(photoModels)
        }
    }

    private lateinit var vm: BodyMeasureEditFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrainingDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        // ViewModelにapplication設定
        vm = BodyMeasureEditFormViewModel(UserPreferenceRepository(this), formType)
        vm.intent = intent
        vm.application = application
        vm.measureTime = captureDateTime

        setListener()

        // 体重・身長・体脂肪率のデフォルト値を取得
        vm.fetchTallAndUserPref()

        initPagerAdapter()
        when (formType) {
            FormType.ADD -> {
                // 新規追加の場合は、今の時刻を設定
                binding.trainingTime.editText?.setText(
                    DateUtil.localDateConvertLocalTimeDateToTime(LocalDateTime.now())
                )
            }
            FormType.EDIT -> {
                // 紐づく測定結果、写真を取得してフィールドに設定
                vm.loadBodyMeasure()
            }
        }
        // 日付設定
        binding.dateText.text = DateUtil.localDateConvertJapaneseFormatYearMonthDay(vm.captureDate)
        binding.viewModel = vm

    }

    private fun initPagerAdapter() {
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
        // (編集時）測定結果を画面描画し、写真をロード
        vm.loadedBodyMeasure.observe(this) {
            if (it) {
                binding.trainingTime.editText?.setText(
                    DateUtil.localDateConvertLocalTimeDateToTime(vm.bodyMeasureEntity.capturedTime)
                )
                binding.weight.editText?.setText("${vm.bodyMeasureEntity.weight}kg")
                binding.fat.editText?.setText("${vm.bodyMeasureEntity.fatRate}%")

                // 紐づく写真を取得
                vm.loadPhotos()
            }
        }
        // （追加時）デフォルトの体重と体脂肪率を設定
        vm.fetchedUserPref.observe(this) {
            if (it) {
                binding.weight.editText?.setText("${vm.measureWeight}kg")
                binding.fat.editText?.setText("${vm.measureFat}%")
            }
        }
        // 削除完了後、一覧へ戻る
        vm.deleted.observe(this) {
            if (it) {
                setResult(RESULT_DELETE)
                finish()
            }
        }
        binding.deleteMeasureBtn.setOnClickListener {
            vm.deleteBodyMeasure()
        }
        // カメラフィールド
        binding.camera.setOnClickListener {
            if (vm.deleting) return@setOnClickListener
            val intent = CameraActivity.createCameraActivityIntent(applicationContext)
            cameraActivityLauncher.launch(intent)
        }

        // 戻るボタン
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 計測時刻
        binding.timeField.setOnClickListener {
            val timePickerFragment = TimePickerDialog.createTimePickerDialog(
                vm.measureTime.hour,
                vm.measureTime.minute
            ) { hour, minute ->
                val hourStr = String.format("%02d", hour)
                val minuteStr = String.format("%02d", minute)
                val time = "${hourStr}時${minuteStr}分"
                (it as EditText).setText(time)
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
        binding.weightField.setOnClickListener {
            val weightPickerFragment =
                FloatNumberPickerDialog.createDialog(vm.measureWeight, "kg") { weight ->
                    (it as TextView).text = "${weight}kg"
                    vm.measureWeight = weight
                }
            weightPickerFragment.show(supportFragmentManager, "WeightPicker")
        }

        // 体脂肪率
        binding.fatField.setOnClickListener {
            val fatPickerFragment =
                FloatNumberPickerDialog.createDialog(vm.measureFat, "%") { fat ->
                    (it as TextView).text = "${fat}%"
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
                null,
                vm.tall,
            )
            vm.updateWeightAndFat()
            when (formType) {
                FormType.ADD -> {
                    vm.addPhoto(saveModel)
                    setResult(RESULT_CREATE)
                }
                FormType.EDIT -> {
                    // 測定がロードできていない場合は更新しない
                    if (vm.loadedBodyMeasure.value == false) return@setOnClickListener
                    vm.editPhoto(saveModel)
                    setResult(RESULT_UPDATE)
                }
            }
            finish()
        }

        val photoDeleteAction = View.OnClickListener {
            val position = (it as ImageButton).tooltipText.toString().toInt()
            vm.deletePhoto(position)
        }
        vm.photoList.observe(this) {
            binding.prevImg.adapter = SliderAdapter(
                it.toList(),
                photoDeleteAction
            )
            binding.prevImg.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        const val KEY_CAPTURE_DATE = "CAPTURE_DATE_TIME"
        const val KEY_CAPTURE_TIME = "CAPTURED_TIME"
        private const val FORM_TYPE = "FORM_TYPE"

        const val RESULT_DELETE = 10
        const val RESULT_UPDATE = 11
        const val RESULT_CREATE = 12

        fun createMeasureEditIntent(
            context: Context,
            formType: FormType = FormType.EDIT,
            captureTime: LocalDateTime,
        ): Intent {
            val intent =
                Intent(context.applicationContext, BodyMeasureEditFormActivity::class.java)
            intent.putExtra(FORM_TYPE, formType)
            intent.putExtra(KEY_CAPTURE_DATE, captureTime.toLocalDate())
            intent.putExtra(KEY_CAPTURE_TIME, captureTime)
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
            //登録日の現在時刻
            intent.putExtra(KEY_CAPTURE_TIME, LocalDateTime.of(formDate, LocalTime.now()))
            return intent
        }
    }
}
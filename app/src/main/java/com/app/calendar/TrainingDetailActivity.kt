package com.app.calendar

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.app.calendar.dialog.TimePickerDialog
import com.app.calendar.model.TrainingModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class TrainingDetailActivity: AppCompatActivity() {

    companion object {
        const val INTENT_KEY = "DATE"
        const val INTENT_RESULT_KEY = "INTENT_RESULT_KEY"
        fun createTrainingDetailActivityIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context.applicationContext, TrainingDetailActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }

    // 写真へのUri
    private var photoUri: Uri? = null

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

        val localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        findViewById<TextView>(R.id.date_text).text = localDate.toString()

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

        // 計測時刻
        val measureTime = findViewById<TextView>(R.id.training_time)
        measureTime.setOnClickListener {
            val timePickerFragment = TimePickerDialog.createTimePickerDialog(1,1) {hour, minute ->
                val hourStr = String.format("%02d", hour)
                val minuteStr =String.format("%02d", minute)
                val time = "${hourStr}時${minuteStr}分"
                (it as TextView).text = time
            }
            timePickerFragment.show(supportFragmentManager, "TimePicker")
        }

        // 保存ボタン
        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            // TODO: テキストではないくスピナー
            val trainingTime = measureTime
            val weight = findViewById<EditText>(R.id.weight).text
            val fatRate = findViewById<EditText>(R.id.fat).text

            var savedWeight = 0.0F
            var savedFatRate = 0.0F
            if(weight.isNotEmpty()) {
                savedWeight = weight.toString().toFloat()
            }
            if(fatRate.isNotEmpty()) {
                savedFatRate = fatRate.toString().toFloat()
            }

            val saveModel = TrainingModel(
                localDate,
                LocalDateTime.now(),
                savedWeight,
                savedFatRate,
                photoUri?.path
            )
            // TODO: DBに保存
            intent.putExtra(INTENT_RESULT_KEY, saveModel)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}
package com.app.calendar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.app.calendar.model.TrainingModel
import java.time.LocalDate
import java.time.LocalDateTime

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
    private var photoUris: MutableList<Uri> = mutableListOf()

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val activityResult = it.data?.extras?.get(CameraActivity.INTENT_KEY_PHOTO_URI)
            if(activityResult != null) {
                photoUris.add(activityResult as Uri)
                findViewById<ImageView>(R.id.prev_img).setImageURI(photoUris[0])
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_detail)

        val localDate = intent.getSerializableExtra(INTENT_KEY) as LocalDate
        findViewById<TextView>(R.id.date_text).text = localDate.toString()

        val cameraStartButton = findViewById<Button>(R.id.camera_button)
        cameraStartButton.setOnClickListener {
            val intent = CameraActivity.createCameraActivityIntent(applicationContext)
            cameraActivityLauncher.launch(intent)
        }

        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            // TODO: テキストではないくスピナー
            val trainingTime = findViewById<EditText>(R.id.training_time)
            val weight = findViewById<EditText>(R.id.weight).toString().toFloat()
            val fatRate = findViewById<EditText>(R.id.fat).text.toString().toFloat()
            val saveModel = TrainingModel(
                localDate,
                LocalDateTime.now(),
                weight,
                fatRate,
                photoUris
            )
            // TODO: DBに保存

            intent.putExtra(INTENT_RESULT_KEY, saveModel)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
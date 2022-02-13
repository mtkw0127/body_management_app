package com.app.calendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class TrainingDetailActivity: AppCompatActivity() {

    companion object {
        const val INTENT_KEY = "DATE"
        fun createTrainingDetailActivityIntent(context: Context, localDate: LocalDate): Intent {
            val intent = Intent(context.applicationContext, TrainingDetailActivity::class.java)
            intent.putExtra(INTENT_KEY, localDate)
            return intent
        }
    }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {

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
    }
}
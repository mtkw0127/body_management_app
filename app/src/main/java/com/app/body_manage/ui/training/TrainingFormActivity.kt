package com.app.body_manage.ui.training

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.data.model.Training
import java.time.LocalDateTime

class TrainingFormActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TrainingFormViewModel()

        setContent {
            TrainingFormScreen(
                training = Training(
                    id = Training.NEW_ID,
                    dateTime = LocalDateTime.now(),
                    menus = listOf(),
                    memo = "メモ".repeat(5)
                ),
                onClickBackPress = ::finish
            )
        }
    }

    companion object {
        fun createInstance(context: Context): Intent {
            return Intent(context, TrainingFormActivity::class.java)
        }
    }
}

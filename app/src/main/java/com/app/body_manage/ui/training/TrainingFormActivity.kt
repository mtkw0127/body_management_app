package com.app.body_manage.ui.training

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.data.model.Training
import java.time.LocalDate
import java.time.LocalTime

class TrainingFormActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TrainingFormViewModel()

        setContent {
            TrainingFormScreen(
                training = Training(
                    id = Training.NEW_ID,
                    date = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    menus = listOf(),
                    memo = "メモ".repeat(5)
                ),
                onClickInputAll = {},
                onClickRegister = {},
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

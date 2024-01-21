package com.app.body_manage.ui.training

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class TrainingFormActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainingFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TrainingFormViewModel()

        setContent {
            TrainingFormScreen(
                day = LocalDate.now(),
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

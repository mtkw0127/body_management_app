package com.app.body_manage.ui.choosePhoto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.repository.BodyMeasurePhotoRepository

class ChoosePhotoActivity : AppCompatActivity() {

    private val bodyMeasurePhotoRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = ChoosePhotoViewModel(bodyMeasurePhotoRepository)
        vm.loadCurrentMonthHavePhotosDateList()
        setContent {
            val uiState: SelectPhotoState by vm.uiState.collectAsState()
            ChoosePhotoScreen(
                state = uiState,
                onSelectDate = {
                    vm.setLocalDate(it)
                },
                clickPhoto = {
                    val intent = Intent()
                    intent.putExtra(RESULT_SELECT_PHOTO_ID, it)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                },
                onChangeCurrentMonth = {
                    vm.updateCurrentMonth(it)
                },
                onClickBackButton = {
                    finish()
                }
            )
        }
    }

    companion object {
        const val RESULT_SELECT_PHOTO_ID = "RESULT_SELECT_PHOTO_ID"
        fun createIntent(context: Context): Intent =
            Intent(context, ChoosePhotoActivity::class.java)
    }
}
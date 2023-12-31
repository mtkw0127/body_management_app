package com.app.body_manage.ui.measure.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.model.PhotoModel
import com.app.body_manage.ui.camera.CameraActivity
import com.app.body_manage.ui.photoDetail.PhotoDetailActivity
import java.time.LocalDateTime

class BodyMeasureEditFormActivity : AppCompatActivity() {
    // 更新前の測定日時
    private val captureDateTime: LocalDateTime by lazy {
        intent.getSerializableExtra(
            KEY_CAPTURE_TIME
        ) as? LocalDateTime ?: LocalDateTime.now() // ランチャーの場合は今日
    }

    private val formType: FormType by lazy {
        intent.getSerializableExtra(FORM_TYPE) as? FormType ?: FormType.ADD // ランチャーの場合は追加
    }

    // カメラ撮影結果コールバック
    private val cameraActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val photoList = CameraActivity.photoList.toList()
            val photoModels =
                photoList.map { uri -> PhotoModel(uri = uri) }.toList()
            viewModel.addPhotos(photoModels)
        }
    }

    // 写真詳細への遷移
    private val photoDetailLauncher = registerForActivityResult(StartActivityForResult()) {}

    private lateinit var viewModel: BodyMeasureEditFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUp()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            BodyMeasureFormScreen(
                uiState = uiState,
                onClickBackPress = { finish() },
                onClickTakePhoto = {
                    cameraActivityLauncher.launch(CameraActivity.createCameraActivityIntent(this))
                },
                onClickSave = {
                    viewModel.save()
                    finish()
                },
                onClickPhotoDetail = {
                    photoDetailLauncher.launch(PhotoDetailActivity.createIntent(this, it.id))
                },
                onClickDeletePhoto = {
                    viewModel.deletePhoto(it)
                },
                onChangeWeightDialog = { isShown ->
                    viewModel.setWeightDialogVisibility(isShown)
                },
                onChangeFatDialog = { isShown ->
                    viewModel.setFatDialogVisibility(isShown)
                }
            )
        }
    }

    private fun setUp() {
        viewModel = BodyMeasureEditFormViewModel(UserPreferenceRepository(this), application)
        when (formType) {
            FormType.ADD -> {
                viewModel.setType(FormViewModelState.Type.Add)
                viewModel.loadFromUserPref()
            }

            FormType.EDIT -> {
                viewModel.loadBodyMeasure(captureDateTime)
                viewModel.setType(FormViewModelState.Type.Edit)
            }
        }
    }

    companion object {
        private const val FORM_TYPE = "FORM_TYPE"
        private const val KEY_CAPTURE_TIME = "KEY_CAPTURE_TIME"

        enum class FormType {
            ADD, EDIT
        }

        fun createMeasureEditIntent(
            context: Context,
            formType: FormType = FormType.EDIT,
            captureTime: LocalDateTime,
        ): Intent {
            return Intent(context, BodyMeasureEditFormActivity::class.java).apply {
                putExtra(FORM_TYPE, formType)
                putExtra(KEY_CAPTURE_TIME, captureTime)
            }
        }

        fun createMeasureFormIntent(
            context: Context,
            formType: FormType = FormType.ADD,
        ): Intent {
            return Intent(context, BodyMeasureEditFormActivity::class.java).apply {
                putExtra(FORM_TYPE, formType)
            }
        }
    }
}

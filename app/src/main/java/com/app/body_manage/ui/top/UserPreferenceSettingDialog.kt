package com.app.body_manage.ui.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.window.Dialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.data.repository.LogRepository
import com.app.body_manage.data.repository.LogRepository.Companion.KEY_INITIAL_DIALOG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserPreferenceSettingDialog : DialogFragment() {
    private lateinit var viewModel: UserPreferenceSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = UserPreferenceSettingViewModel(
            userPreferenceRepository = UserPreferenceRepository(requireContext())
        )
        viewModel.load()
        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                if (it) {
                    this@UserPreferenceSettingDialog.parentFragmentManager.setFragmentResult(
                        REQUEST_KEY,
                        bundleOf()
                    )
                    dismiss()
                }
            }
        }
        val view = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsState()
                Dialog(onDismissRequest = { /* Not to close*/ }) {
                    UserPreferenceSettingScreen(
                        uiState = uiState,
                        onChangeName = viewModel::setName,
                        onChangeGender = viewModel::setGender,
                        onChangeBirth = viewModel::setBirth,
                        onClickSet = {
                            activity?.let {
                                LogRepository().sendLog(
                                    it,
                                    KEY_INITIAL_DIALOG,
                                    Bundle().apply {
                                        putString("name", uiState.name)
                                        putString("gender", uiState.gender.name)
                                        putString("birth", uiState.birth.text)
                                    }
                                )
                            }
                            viewModel.save()
                        },
                        onClickMealOption = viewModel::updateMealFeature,
                        onClickTrainingOption = viewModel::updateTrainingFeature,
                    )
                }
            }
        }
        return view
    }

    companion object {
        const val REQUEST_KEY = "INITIAL_SETTING_DIALOG_KEY"

        fun createInstance(): UserPreferenceSettingDialog {
            return UserPreferenceSettingDialog()
        }
    }
}

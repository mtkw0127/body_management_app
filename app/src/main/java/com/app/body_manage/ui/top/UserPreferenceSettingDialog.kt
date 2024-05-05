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
        viewModel.init(arguments?.getSerializable(LAUNCH_TYPE) as LaunchType)
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
                        onChangeTall = viewModel::setTall,
                        onChangeWeight = viewModel::setWeight,
                        onClickSet = {
                            viewModel.save()
                        },
                    )
                }
            }
        }
        return view
    }

    companion object {
        const val REQUEST_KEY = "INITIAL_SETTING_DIALOG_KEY"
        private const val LAUNCH_TYPE = "LAUNCH_TYPE"

        enum class LaunchType {
            INITIAL_SETTING,
            EDIT_SETTING,
        }

        fun createInstance(
            launchType: LaunchType
        ): UserPreferenceSettingDialog {
            return UserPreferenceSettingDialog().apply {
                arguments = bundleOf(LAUNCH_TYPE to launchType)
            }
        }
    }
}

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
        lifecycleScope.launch {
            viewModel.saved.collectLatest { saved ->
                if (saved) {
                    parentFragmentManager.setFragmentResult("TEST", bundleOf())
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
        fun createInstance(): UserPreferenceSettingDialog {
            return UserPreferenceSettingDialog()
        }
    }
}

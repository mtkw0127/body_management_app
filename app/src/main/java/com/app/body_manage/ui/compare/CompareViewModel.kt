package com.app.body_manage.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.net.URI
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface CompareState {
    data class CompareItemsHasSet(
        val before: CompareItem?,
        val after: CompareItem?,
    ) : CompareState

    data class CompareItemsError(
        val error: Throwable
    ) : CompareState
}

data class CompareViewModelState(
    val before: CompareItem? = null,
    val after: CompareItem? = null,
    val error: Throwable? = null,
) {
    fun toUiSate(): CompareState {
        return if (error != null) {
            CompareState.CompareItemsError(error)
        } else {
            CompareState.CompareItemsHasSet(before, after)
        }
    }
}

data class CompareItem(
    val data: LocalDate,
    val weight: Float,
    val photoURI: URI,
)

class CompareViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(CompareViewModelState())
    val uiState = viewModelState.map {
        it.toUiSate()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CompareState.CompareItemsHasSet(null, null),
    )
}
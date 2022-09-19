package com.app.body_manage.ui.choosePhoto

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.body_manage.common.Calendar
import com.app.body_manage.ui.measure.list.PhotoList
import java.time.LocalDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChoosePhotoScreen(
    state: SelectPhotoState,
    clickPhoto: (Int) -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.HalfExpanded)
    Scaffold {
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(15.dp),
            sheetState = sheetState,
            modifier = Modifier.padding(it),
            sheetContent = {
                when (state) {
                    is SelectPhotoState.SelectedPhoto -> {
                        Box(modifier = Modifier.padding(top = 15.dp)) {
                            PhotoList(
                                state.photoList,
                                clickPhoto
                            )
                        }
                    }
                    is SelectPhotoState.Error -> {
                    }
                }
            }) {
            when (state) {
                is SelectPhotoState.SelectedPhoto -> {
                    Column {
                        Calendar(
                            markDayList = state.currentMonthRegisteredDateList,
                            selectedDate = state.date,
                            onClickDate = { date ->
                                scope.launch {
                                    sheetState.show()
                                }
                                onSelectDate.invoke(date)
                            }
                        )
                    }
                }
                is SelectPhotoState.Error -> {

                }
            }
        }
    }
}
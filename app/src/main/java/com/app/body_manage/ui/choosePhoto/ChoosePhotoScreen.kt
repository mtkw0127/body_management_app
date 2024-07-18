package com.app.body_manage.ui.choosePhoto

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.common.Calendar
import com.app.body_manage.ui.measure.list.PhotoList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChoosePhotoScreen(
    state: SelectPhotoState,
    clickPhoto: (Int) -> Unit,
    onChangeCurrentMonth: (YearMonth) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onClickBackPress: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.HalfExpanded)
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.screen_name_choose_compare_photo),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBackPress) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = colorResource(id = R.color.app_theme)
            )
        }
    ) {
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
            }
        ) {
            when (state) {
                is SelectPhotoState.SelectedPhoto -> {
                    Column {
                        Calendar(
                            markDayList = state.currentMonthRegisteredDateList,
                            selectedDate = state.date,
                            onChangeCurrentMonth = onChangeCurrentMonth,
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

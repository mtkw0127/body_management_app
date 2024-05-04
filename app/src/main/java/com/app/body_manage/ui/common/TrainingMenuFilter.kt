package com.app.body_manage.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.ChipDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.style.Colors

@Composable
fun TrainingMenuFilter(
    onClickPart: (TrainingMenu.Part?) -> Unit,
    onClickType: (TrainingMenu.Type?) -> Unit,
    selectedPart: TrainingMenu.Part?,
    selectedType: TrainingMenu.Type?,
) {
    Row {
        DropDownFilterChipForPart(
            selectedPart = selectedPart,
            onClickPart = onClickPart,
        )

        Spacer(modifier = Modifier.size(10.dp))

        DropDownFilterChipForType(
            selectedType = selectedType,
            onClickType = onClickType,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropDownFilterChipForPart(
    selectedPart: TrainingMenu.Part?,
    onClickPart: (TrainingMenu.Part?) -> Unit,
) {
    val allAddedMenus = listOf(null) + TrainingMenu.Part.entries
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        FilterChip(
            enabled = true,
            selected = true,
            onClick = { expanded = true },
            colors = ChipDefaults.filterChipColors(
                selectedBackgroundColor = Colors.theme,
            ),
        ) {
            Text(
                text = stringResource(
                    id = R.string.label_training_target_part_selected,
                    if (selectedPart != null) {
                        stringResource(selectedPart.nameStringResourceId)
                    } else {
                        stringResource(R.string.label_all)
                    },
                ),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allAddedMenus.forEach { menu ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onClickPart(menu)
                    }
                ) {
                    Text(
                        text = if (menu != null) {
                            stringResource(menu.nameStringResourceId)
                        } else {
                            stringResource(R.string.label_all)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropDownFilterChipForType(
    selectedType: TrainingMenu.Type?,
    onClickType: (TrainingMenu.Type?) -> Unit,
) {
    val allAddedMenus = listOf(null) + TrainingMenu.Type.entries
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        FilterChip(
            enabled = true,
            selected = true,
            onClick = { expanded = true },
            colors = ChipDefaults.filterChipColors(
                selectedBackgroundColor = Colors.theme,
            ),
        ) {
            Text(
                text = stringResource(
                    id = R.string.label_training_target_type_selected,
                    if (selectedType != null) {
                        stringResource(selectedType.nameStringRes)
                    } else {
                        stringResource(R.string.label_all)
                    }
                ),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allAddedMenus.forEach { menu ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onClickType(menu)
                    }
                ) {
                    Text(
                        text = if (menu != null) {
                            stringResource(menu.nameStringRes)
                        } else {
                            stringResource(R.string.label_all)
                        }
                    )
                }
            }
        }
    }
}

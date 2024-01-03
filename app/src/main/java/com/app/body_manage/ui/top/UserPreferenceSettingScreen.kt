package com.app.body_manage.ui.top

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.data.local.Gender
import com.app.body_manage.extension.toYYYYMMDD
import com.app.body_manage.style.Colors.Companion.theme

@Composable
fun UserPreferenceSettingScreen(
    uiState: UiState,
    onChangeName: (String) -> Unit,
    onChangeGender: (Gender) -> Unit,
    onChangeBirth: (String) -> Unit,
    onChangeTall: (String) -> Unit,
    onChangeWeight: (String) -> Unit,
    onClickSet: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .fillMaxWidth(0.95F)
            .fillMaxHeight(0.7F)
            .padding(30.dp)
    ) {
        item {
            Name(uiState.name, onChangeName)
        }
        item {
            Gender(uiState.gender, onChangeGender)
        }
        item {
            Birth(uiState.birth, onChangeBirth)
        }
        item {
            Weight(uiState.weight, onChangeWeight)
        }
        item {
            Tall(uiState.tall, onChangeTall)
        }
        item {
            Spacer(modifier = Modifier.size(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                CustomButton(
                    onClick = { onClickSet() },
                    valueResourceId = R.string.settings,
                    backgroundColor = theme,
                    enable = uiState is UiState.Done
                )
            }
        }
    }
}

@Composable
private fun Gender(
    gender: Gender,
    onChangeGender: (Gender) -> Unit
) {
    Label(R.string.gender)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        RadioButton(selected = gender == Gender.MALE, onClick = { onChangeGender(Gender.MALE) })
        Text(text = stringResource(id = R.string.gender_male))
        RadioButton(selected = gender == Gender.FEMALE, onClick = { onChangeGender(Gender.FEMALE) })
        Text(text = stringResource(id = R.string.gender_female))
    }
}

@Composable
private fun Birth(birthText: String?, onChangeBirth: (String) -> Unit) {
    Label(R.string.birth)
    CustomTextField(
        value = birthText.orEmpty(),
        onValueChange = onChangeBirth,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_birth))
        },
        visualTransformation = { text ->
            TransformedText(
                AnnotatedString(text.text.toYYYYMMDD()),
                object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        val charNum = if (offset in 0..3) {
                            0
                        } else if (offset in 4..5) {
                            1
                        } else {
                            2
                        }
                        return offset + charNum
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        var charNum = (offset - 4) / 2
                        if (charNum < 0) charNum = 0
                        return offset - charNum
                    }
                }
            )
        }
    )
}

@Composable
private fun Name(string: String?, onChangeName: (String) -> Unit) {
    Label(R.string.label_user_name)
    CustomTextField(
        value = string.orEmpty(),
        onValueChange = onChangeName,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_user_name))
        },
        keyboardType = KeyboardType.Text,
    )
}

@Composable
private fun Tall(value: Float?, onChangeTall: (String) -> Unit) {
    Label(R.string.tall)
    CustomTextField(
        value = value?.toString() ?: "",
        onValueChange = onChangeTall,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_tall))
        },
    )
}

@Composable
private fun Weight(value: Float?, onChangeWeight: (String) -> Unit) {
    Label(R.string.current_weight)
    CustomTextField(
        value = value?.toString() ?: "",
        onValueChange = onChangeWeight,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_current_weight))
        },
    )
}

@Composable
private fun Label(textResource: Int) {
    Text(text = stringResource(textResource), modifier = Modifier.padding(10.dp))
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Number,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        placeholder = placeholder,
        visualTransformation = visualTransformation,
    )
}

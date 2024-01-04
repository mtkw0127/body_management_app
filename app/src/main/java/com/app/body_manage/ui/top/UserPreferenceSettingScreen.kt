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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
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
    onChangeName: (TextFieldValue) -> Unit,
    onChangeGender: (Gender) -> Unit,
    onChangeBirth: (TextFieldValue) -> Unit,
    onChangeTall: (TextFieldValue) -> Unit,
    onChangeWeight: (TextFieldValue) -> Unit,
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
private fun Birth(birthText: TextFieldValue?, onChangeBirth: (TextFieldValue) -> Unit) {
    Label(R.string.birth)
    CustomTextField(
        value = birthText,
        onValueChange = onChangeBirth,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_birth))
        },
        visualTransformation = { text ->
            val transformedText = text.text.toYYYYMMDD()
            TransformedText(
                AnnotatedString(transformedText),
                object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return transformedText.length
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        var length = transformedText.length
                        if (transformedText.contains("年")) length = -1
                        if (transformedText.contains("月")) length = -1
                        if (transformedText.contains("日")) length = -1
                        return length
                    }
                }
            )
        }
    )
}

@Composable
private fun Name(string: String?, onChangeName: (TextFieldValue) -> Unit) {
    Label(R.string.label_user_name)
    CustomTextField(
        value = TextFieldValue(string.orEmpty(), selection = TextRange(string?.length ?: 0)),
        onValueChange = onChangeName,
        placeholder = {
            Text(
                text = stringResource(id = R.string.placeholder_user_name)
            )
        },
        keyboardType = KeyboardType.Text,
        singleLine = true,
    )
}

@Composable
private fun Tall(value: TextFieldValue, onChangeTall: (TextFieldValue) -> Unit) {
    Label(R.string.tall)
    CustomTextField(
        value = value,
        onValueChange = onChangeTall,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_tall))
        },
        visualTransformation = { text ->
            val textWithUnit = if (text.text.isNotBlank()) {
                text.text + "cm"
            } else ""
            TransformedText(
                AnnotatedString(textWithUnit),
                object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return textWithUnit.length
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        val length = text.text.length
                        return if (length == 0) 0 else length - 2
                    }
                }
            )
        }
    )
}

@Composable
private fun Weight(value: TextFieldValue, onChangeWeight: (TextFieldValue) -> Unit) {
    Label(R.string.current_weight)
    CustomTextField(
        value = value,
        onValueChange = onChangeWeight,
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_current_weight))
        },
        visualTransformation = { text ->
            val textWithUnit = if (text.text.isNotBlank()) {
                text.text + "kg"
            } else ""
            TransformedText(
                AnnotatedString(textWithUnit),
                object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return textWithUnit.length
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        val length = text.text.length
                        return if (length == 0) 0 else length - 2
                    }
                }
            )
        }
    )
}

@Composable
private fun Label(textResource: Int) {
    Text(text = stringResource(textResource), modifier = Modifier.padding(10.dp))
}

@Composable
private fun CustomTextField(
    value: TextFieldValue?,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: @Composable () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Number,
    singleLine: Boolean = false,
) {
    TextField(
        value = value ?: TextFieldValue(""),
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        placeholder = placeholder,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
    )
}

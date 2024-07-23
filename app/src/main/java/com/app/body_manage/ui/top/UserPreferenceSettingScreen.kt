package com.app.body_manage.ui.top

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onChangeBirth: (TextFieldValue) -> Unit,
    onClickSet: () -> Unit,
    onClickMealOption: (Boolean) -> Unit,
    onClickTrainingOption: (Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .fillMaxWidth(0.95F)
            .wrapContentHeight()
            .padding(30.dp)
    ) {
        item {
            Name(uiState.name.orEmpty(), onChangeName)
        }
        item {
            Birth(uiState.birth, onChangeBirth)
        }
        item {
            Gender(uiState.gender, onChangeGender)
        }
        item {
            Column {
                Label(R.string.label_option)
                Text(
                    text = stringResource(id = R.string.label_option_describe),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.clickable {
                        onClickMealOption(!uiState.hasMealFeature)
                    }
                ) {
                    Checkbox(
                        checked = uiState.hasMealFeature,
                        onCheckedChange = onClickMealOption,
                    )
                    Text(text = stringResource(id = R.string.label_meal_feature))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.clickable {
                        onClickTrainingOption(!uiState.hasTrainingFeature)
                    }
                ) {
                    Checkbox(
                        checked = uiState.hasTrainingFeature,
                        onCheckedChange = onClickTrainingOption,
                    )
                    Text(text = stringResource(id = R.string.label_training_feature))
                }
            }
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
        singleLine = true,
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
                        if (transformedText.contains("年")) length -= 1
                        if (transformedText.contains("月")) length -= 1
                        if (transformedText.contains("日")) length -= 1
                        return length
                    }
                }
            )
        }
    )
}

@Composable
private fun Name(name: String, onChangeName: (String) -> Unit) {
    Label(R.string.label_user_name)
    TextField(
        value = name,
        onValueChange = onChangeName,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.placeholder_user_name)
            )
        },
        visualTransformation = VisualTransformation.None,
        singleLine = true,
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

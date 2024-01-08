package com.app.body_manage.ui.mealForm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.common.toKcal
import com.app.body_manage.data.model.Food
import com.app.body_manage.data.model.Meal
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.extension.withKcal
import com.app.body_manage.style.Colors
import java.time.LocalDateTime

@Composable
fun MealFormScreen(
    type: MealFormViewModel.Type,
    mealFoods: Meal,
    foodCandidates: List<Food>,
    onClickMealTiming: (Meal.Timing) -> Unit,
    onClickTime: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onClickSearchedFood: (Food) -> Unit,
    onClickBackPress: () -> Unit,
    onClickSave: () -> Unit,
    onClickDeleteFood: (Food) -> Unit,
    onClickTakePhoto: () -> Unit,
    onClickDeleteForm: () -> Unit,
    onUpdateMealKcal: (Food, Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Colors.theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mealFoods.time.toLocalDate().toMMDDEE(),
                        modifier = Modifier.offset(x = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (type == MealFormViewModel.Type.Edit) {
                        Spacer(modifier = Modifier.weight(1F))
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            tint = Color.Black,
                            contentDescription = null,
                            modifier = Modifier.clickable { onClickDeleteForm() }
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                SelectMealTiming(mealFoods.timing, onClickMealTiming)
                Spacer(modifier = Modifier.size(10.dp))
                Time(mealFoods.time, onClickTime)
                Spacer(modifier = Modifier.size(10.dp))
                EatenFoods(
                    mealFoods.foods,
                    onClickDeleteFood,
                    onUpdateMealKcal
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            TextWithCandidate(
                onValueChange = onSearchTextChange,
                onClickSearchedFood = onClickSearchedFood,
                candidates = foodCandidates,
            )
            SaveForm(
                onClickBackPress = onClickBackPress,
                onClickSave = onClickSave,
                onClickTakePhoto = onClickTakePhoto,
                enable = mealFoods.foods.isNotEmpty(),
            )
        }
    }
}

@Composable
private fun Time(
    time: LocalDateTime,
    onClickTime: () -> Unit,
) {
    Box(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        IconAndText(
            text = time.toJapaneseTime(),
            onClick = onClickTime,
        )
    }
}

@Composable
private fun SelectMealTiming(
    timing: Meal.Timing,
    onClickMealTiming: (Meal.Timing) -> Unit
) {
    var mealTimingMenuExpanded by remember { mutableStateOf(false) }
    val mealTimingMenuItems = Meal.Timing.entries.toList()

    Box(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        IconAndText(
            text = stringResource(timing.textResourceId),
            onClick = {
                mealTimingMenuExpanded = true
            },
        )
        DropdownMenu(
            expanded = mealTimingMenuExpanded,
            onDismissRequest = { mealTimingMenuExpanded = false }
        ) {
            mealTimingMenuItems.forEach { timing ->
                DropdownMenuItem(onClick = {
                    onClickMealTiming(timing)
                    mealTimingMenuExpanded = false
                }) {
                    Text(text = stringResource(id = timing.textResourceId))
                }
            }
        }
    }
}

@Composable
private fun EatenFoods(
    foods: List<Food>,
    onClickDeleteFood: (Food) -> Unit,
    onUpdateMealKcal: (Food, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(2.dp)
            .background(
                Color.White,
                RoundedCornerShape(5.dp)
            )
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.label_all_kcal),
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = foods.sumOf { checkNotNull(it.kcal) }.toKcal()
            )
        }
        Divider(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
        )
        foods.forEach { food ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = food.name)
                Spacer(modifier = Modifier.weight(1F))
                TextField(
                    value = food.kcal.toString(),
                    onValueChange = { value ->
                        try {
                            val numberString = value.ifEmpty { "0" }
                            var number = numberString.toInt()
                            if (
                                numberString.isEmpty().not() &&
                                numberString.contains(" ").not() &&
                                numberString.contains(",").not() &&
                                numberString.contains("-").not() &&
                                numberString.contains(".").not() &&
                                numberString.contains("_").not()
                            ) {
                                if (food.kcal == 0) {
                                    number = numberString.first().toString().toInt()
                                }
                                onUpdateMealKcal(food, number)
                            }
                        } catch (e: Throwable) {
                            return@TextField
                        }
                    },
                    colors = textFieldColors(
                        backgroundColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp),
                    visualTransformation = {
                        val annotatedString = AnnotatedString(it.text.withKcal())
                        TransformedText(
                            annotatedString,
                            object : OffsetMapping {
                                override fun originalToTransformed(offset: Int): Int {
                                    return annotatedString.length - 5
                                }

                                override fun transformedToOriginal(offset: Int): Int {
                                    return food.kcal.toString().length
                                }
                            }
                        )
                    }
                )
                Spacer(modifier = Modifier.size(10.dp))
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.clickable { onClickDeleteFood(food) }
                )
            }
        }
    }
}

@Composable
private fun TextWithCandidate(
    onValueChange: (String) -> Unit,
    onClickSearchedFood: (Food) -> Unit,
    candidates: List<Food>
) {
    var searchText by remember { mutableStateOf("") }
    Column(modifier = Modifier.imePadding()) {
        val roundedConnerShape = RoundedCornerShape(
            topStart = CornerSize(15.dp),
            topEnd = CornerSize(15.dp),
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        )
        LazyColumn(
            modifier = Modifier
                .offset(0.dp, 0.5.dp)
                .fillMaxWidth(1F)
                .shadow(0.5.dp, roundedConnerShape)
                .border(0.5.dp, Color.LightGray, roundedConnerShape)
                .background(Color.White, roundedConnerShape)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = stringResource(R.string.label_result_of_search),
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(top = 15.dp)
                    )
                }
            }
            itemsIndexed(candidates) { _, candidate ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onClickSearchedFood(candidate)
                            searchText = ""
                            onValueChange("")
                        }
                        .height(50.dp)
                        .padding(vertical = 5.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = candidate.name + " (${candidate.kcal.toKcal()})")
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = stringResource(id = R.string.message_save_and_add),
                        color = Color.DarkGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
        BasicTextField(
            value = searchText,
            textStyle = TextStyle(
                lineHeight = 150.sp
            ),
            modifier = Modifier
                .offset(0.dp, 0.5.dp)
                .fillMaxWidth(),
            onValueChange = {
                searchText = it
                onValueChange(it)
            },
            decorationBox = @Composable { innerTextField ->
                Box(
                    modifier = Modifier
                        .border(0.5.dp, Color.LightGray)
                        .padding(10.dp)
                ) {
                    if (searchText.isBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = stringResource(id = R.string.message_search_and_add),
                                color = Color.LightGray,
                            )
                        }
                    }
                    innerTextField()
                    if (searchText.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(modifier = Modifier.weight(1F))
                            Icon(
                                imageVector = Icons.Sharp.Cancel,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    searchText = ""
                                    onValueChange("")
                                }
                            )
                        }
                    }
                }
            },
            singleLine = true,
        )
    }
}

@Composable
private fun SaveForm(
    onClickBackPress: () -> Unit,
    onClickSave: () -> Unit,
    onClickTakePhoto: () -> Unit,
    enable: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Colors.background)
            .shadow(0.5.dp, clip = true),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomButton(onClickBackPress, R.string.back, Color.White)
            Spacer(modifier = Modifier.weight(1F))
            CustomButton(onClickSave, R.string.save, Colors.theme, enable = enable)
            Spacer(modifier = Modifier.size(20.dp))
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = null,
                modifier = Modifier.clickable { onClickTakePhoto() }
            )
        }
    }
}

@Composable
private fun IconAndText(
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(5.dp))
        Column {
            Text(text = text)
        }
    }
}

package com.app.body_manage.ui.trainingForm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.data.model.Training
import com.app.body_manage.data.model.TrainingMenu
import com.app.body_manage.data.model.createSampleOwnWeightTrainingMenu
import com.app.body_manage.data.model.createSampleTrainingMenu
import com.app.body_manage.extension.toJapaneseTime
import com.app.body_manage.extension.toMMDDEE
import com.app.body_manage.style.Colors.Companion.background
import com.app.body_manage.style.Colors.Companion.theme
import com.app.body_manage.ui.top.PanelColumn
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TrainingFormScreen(
    training: Training,
    onClickBackPress: () -> Unit = {},
    onClickRegister: () -> Unit = {},
    onClickFab: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = theme) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickBackPress() },
                        tint = Color.Black
                    )
                    Text(
                        text = training.date.toMMDDEE(),
                        modifier = Modifier.offset(x = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(theme)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable {
                        onClickRegister()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(id = R.string.label_register_trainning))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onClickFab()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
                .padding(horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Row {
                    Text(text = stringResource(id = R.string.label_start_training_time))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = training.startTime.toJapaneseTime())
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Text(text = stringResource(id = R.string.label_end_training_time))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = training.endTime.toJapaneseTime())
                }
            }

            Spacer(modifier = Modifier.size(5.dp))

            PanelColumn {
                Text(text = stringResource(id = R.string.label_memo_area_training))
                MemoTextField(training.memo)
            }

            Spacer(modifier = Modifier.size(5.dp))

            training.menus.forEachIndexed { index, menu ->
                TrainingPanel {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_event, index + 1),
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(5.dp))
                                .padding(5.dp),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = menu.name)
                        Spacer(modifier = Modifier.weight(1F))
//                        CustomButton(
//                            onClick = onClickInputAll,
//                            valueResourceId = R.string.label_input_all,
//                            fontSize = 11.sp,
//                            backgroundColor = theme,
//                            modifier = Modifier.padding(1.dp)
//                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))

                    val chunkedMenu = menu.sets.chunked(3)
                    chunkedMenu.forEachIndexed { index, chunk ->
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(50.dp)
                            ) {
                                Text(
                                    text = "",
                                    modifier = Modifier.padding(3.dp)
                                )
                                Text(
                                    text = stringResource(id = R.string.label_result),
                                    modifier = Modifier.padding(3.dp)
                                )
                                if (menu.type == TrainingMenu.Type.MACHINE || menu.type == TrainingMenu.Type.FREE) {
                                    Text(
                                        text = stringResource(id = R.string.label_weight),
                                        modifier = Modifier.padding(3.dp)
                                    )
                                }
                            }
                            chunk.forEach { set ->
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(100.dp)
                                ) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.label_set,
                                            set.index + 1
                                        ),
                                        modifier = Modifier.padding(3.dp)
                                    )
                                    CountTextField(set.number, R.string.label_count)
                                    Spacer(modifier = Modifier.size(7.dp))
                                    // 重りを扱う種目の場合は重量を入力する
                                    if (set is TrainingMenu.WeightSet) {
                                        CountTextField(set.weight, R.string.label_weight_unit)
                                    }
                                }
                            }
                        }
                        if (index != chunkedMenu.lastIndex) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    MemoTextField(menu.memo)
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun TrainingPanel(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(15.dp))
            .background(
                Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(15.dp)
    ) {
        content()
    }
}

@Composable
private fun MemoTextField(
    text: String,
) {
    var value by remember { mutableStateOf(text) }
    Column {
        Spacer(modifier = Modifier.size(5.dp))
        BasicTextField(
            value = value,
            onValueChange = {
                value = it
            },
            maxLines = 10,
            modifier = Modifier
                .heightIn(min = 80.dp)
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(1.dp))
                        .padding(5.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.label_memo_area),
                            color = Color.LightGray
                        )
                    } else {
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
private fun CountTextField(count: Int, unitStringResource: Int) {
    var number by remember { mutableStateOf(count.toString()) }
    val unit = stringResource(id = unitStringResource)
    val empty = stringResource(id = R.string.label_empty)
    val displayNumber = remember(number) {
        if (number.isNotBlank()) {
            "$number$unit"
        } else {
            empty
        }
    }
    BasicTextField(
        value = number,
        onValueChange = {
            if (it.isBlank()) {
                number = ""
                return@BasicTextField
            }
            try {
                if (it.toInt() < 1000) {
                    number = it
                }
            } catch (e: NumberFormatException) {
                Timber.e(e)
            }
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        visualTransformation = {
            TransformedText(
                text = AnnotatedString(displayNumber),
                offsetMapping = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return displayNumber.length
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        return number.length
                    }
                }
            )
        },
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
            ) {
                innerTextField()
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@Preview
private fun TrainingFormPreview() {
    TrainingFormScreen(
        training = Training(
            id = Training.NEW_ID,
            date = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            menus = listOf(
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
                createSampleTrainingMenu(),
                createSampleOwnWeightTrainingMenu(),
                createSampleOwnWeightTrainingMenu(),
            ),
            memo = "たくさん頑張った".repeat(5)
        ),
        onClickBackPress = {},
    )
}

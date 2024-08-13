package com.app.body_manage.ui.graph

import android.text.SpannableStringBuilder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.style.Colors.Companion.disable
import com.app.body_manage.style.Colors.Companion.theme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape.Companion.rounded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@Composable
fun GraphScreen(
    state: GraphState,
    onClickDataType: (DataType) -> Unit,
    onClickDuration: (Duration) -> Unit,
    onClickBack: () -> Unit,
) {
    Scaffold {
        Box(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(it)
                .fillMaxSize(),
        ) {
            Column(modifier = Modifier.padding(start = 5.dp)) {
                when (state) {
                    is GraphState.HasData -> {
                        FilterChipsContainer(
                            onClickDuration = onClickDuration,
                            onClickDataType = onClickDataType,
                            state = state,
                            onClickBack = onClickBack,
                        )

                        Graph(state)
                    }

                    GraphState.NoData -> {
                        NoGraph()
                    }

                    GraphState.Initial -> {}
                }
            }
        }
    }
}

@Composable
private fun NoGraph() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.message_empty),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Graph(state: GraphState.HasData) {
    // 横軸の日付フォーマット
    val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MM月dd日")

    val dataSet = when (state.currentType) {
        DataType.WEIGHT -> state.timelineForWeight
        DataType.FAT -> state.timelineForFat
    }

    val x = List(dataSet.size) { index -> index }
    val y = dataSet.map { it.second }

    val modelProducer = remember { CartesianChartModelProducer() }

    suspend fun createModel() = withContext(Dispatchers.Default) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x,
                    y,
                )
            }
        }
    }

    LaunchedEffect(state.currentType) {
        createModel()
    }

    LaunchedEffect(state.duration) {
        createModel()
    }

    val bottomFormatter = CartesianValueFormatter { value, chartValues, verticalAxisPosition ->
        value.toInt().let { index ->
            state.timelineForWeight.getOrNull(index)?.first?.format(
                dateTimeFormatter
            ) ?: "error"
        }
    }

    CartesianChartHost(
        modifier = Modifier.fillMaxSize(),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(remember { LineCartesianLayer.LineFill.single(fill(Color(0xffa485e0))) })
                )
            ),
            getXStep = { state.duration.duration },
            startAxis = rememberStartAxis(
                title = when (state.currentType) {
                    DataType.WEIGHT -> {
                        "体重 [kg]"
                    }

                    DataType.FAT -> {
                        "体脂肪率 [%]"
                    }
                },
                titleComponent = TextComponent()
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberTextComponent(
                    lineCount = 2,
                ),
                valueFormatter = bottomFormatter
            ),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(
                    color = Color.Black,
                    background = ShapeComponent(
                        color = android.graphics.Color.WHITE,
                        shape = rounded(allDp = 3F)
                    ),
                    lineCount = 2,
                ),
                valueFormatter = remember {
                    CartesianMarkerValueFormatter { _, targets ->
                        SpannableStringBuilder().apply {
                            targets.forEachIndexed { index, target ->
                                dataSet[target.x.toInt()].let { data ->
                                    data.first.format(dateTimeFormatter).let {
                                        append(it)
                                        append("\n")
                                        append("${data.second} kg")
                                    }
                                }
                                if (index != targets.lastIndex) append(", ")
                            }
                        }
                    }
                },
                guideline = rememberAxisGuidelineComponent(),
            ),
            persistentMarkers = null,
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
private fun FilteringChips(
    onClickBack: () -> Unit,
    onClickDuration: (Duration) -> Unit,
    onClickDataType: (DataType) -> Unit,
    state: GraphState.HasData,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier.clickable { onClickBack() }
        )
        Spacer(modifier = Modifier.width(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDuration(Duration.ONE_MONTH) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.duration == Duration.ONE_MONTH) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.label_one_month))
        }
        Spacer(modifier = Modifier.size(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDuration(Duration.THREE_MONTH) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.duration == Duration.THREE_MONTH) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.label_three_month))
        }
        Spacer(modifier = Modifier.size(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDuration(Duration.HALF_YEAR) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.duration == Duration.HALF_YEAR) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.label_six_month))
        }
        Spacer(modifier = Modifier.size(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDuration(Duration.ONE_YEAR) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.duration == Duration.ONE_YEAR) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.label_one_year))
        }
        Spacer(modifier = Modifier.size(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDuration(Duration.ALL) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.duration == Duration.ALL) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.label_all_duration))
        }
    }
    FlowRow {
        Spacer(modifier = Modifier.width(30.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDataType(DataType.WEIGHT) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.currentType == DataType.WEIGHT) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.weight))
        }
        Spacer(modifier = Modifier.size(5.dp))
        FilterChip(
            selected = false,
            onClick = { onClickDataType(DataType.FAT) },
            colors = ChipDefaults.filterChipColors(
                backgroundColor = if (state.currentType == DataType.FAT) theme else disable
            )
        ) {
            Text(text = stringResource(id = R.string.fat))
        }
    }
}

@Composable
private fun FilterChipsContainer(
    state: GraphState.HasData,
    onClickBack: () -> Unit,
    onClickDuration: (Duration) -> Unit,
    onClickDataType: (DataType) -> Unit,
) {
    var isOpen by remember { mutableStateOf(true) }
    Row {
        if (isOpen) {
            Column {
                FilteringChips(
                    onClickDuration = onClickDuration,
                    onClickDataType = onClickDataType,
                    state = state,
                    onClickBack = onClickBack,
                )
                Text(text = stringResource(id = R.string.message_recent_data_is_shown))
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        Icon(
            imageVector = if (isOpen) {
                Icons.Default.KeyboardArrowUp
            } else {
                Icons.Default.KeyboardArrowDown
            },
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    isOpen = isOpen.not()
                }
                .padding(10.dp)
        )
    }
}

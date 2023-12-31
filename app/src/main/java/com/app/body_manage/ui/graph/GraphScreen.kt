package com.app.body_manage.ui.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.style.Colors.Companion.disable
import com.app.body_manage.style.Colors.Companion.secondPrimary
import com.app.body_manage.style.Colors.Companion.theme
import com.patrykandpatrick.vico.compose.axis.axisLineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GraphScreen(
    state: GraphState,
    bottomSheetDataList: List<BottomSheetData>,
    onClickDataType: (DataType) -> Unit,
    onClickDuration: (Duration) -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomSheet(bottomSheetDataList)
        },
    ) { it ->
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            Column(modifier = Modifier.padding(start = 5.dp)) {
                when (state) {
                    is GraphState.HasData -> {
                        FilteringChips(
                            onClickDuration = onClickDuration,
                            onClickDataType = onClickDataType,
                            state = state,
                        )

                        Text(text = stringResource(id = R.string.message_recent_data_is_shown))

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
            color = Color.Gray,
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

    // Min/MaxY
    val minY = dataSet.minBy { it.second }.second
    val maxY = dataSet.maxBy { it.second }.second

    // 横軸を原点からデータをスタートするためにminXで引く
    val model = entryModelOf(dataSet.mapIndexed { _, pair ->
        FloatEntry(pair.first.toEpochDay().toFloat(), pair.second)
    }.sortedBy { it.x })
    Chart(
        modifier = Modifier.fillMaxSize(),
        chart = lineChart(
            lines = listOf(
                lineSpec(
                    lineColor = secondPrimary,
                    point = ShapeComponent(shape = pillShape),
                    pointSize = 5.dp,
                    dataLabel = TextComponent.Builder().build() // 各点の側に値を表示する
                )
            ),
            // 縦軸の最大・最小は例えば最低・最低体重の±3kgとする
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = minY - 3,
                maxY = maxY + 3,
            )
        ),
        marker = MarkerComponent(
            label = TextComponent.Builder().build(),
            indicator = ShapeComponent(),
            guideline = lineComponent(
                color = theme,
                thickness = 2.dp,
            )
        ),
        model = model,
        startAxis = rememberStartAxis(
            axis = axisLineComponent(
                strokeWidth = 1.dp,
                strokeColor = Color.Black
            ), // 縦軸をはっきりさせる
            title = stringResource(id = R.string.weight_unit),
            titleComponent = TextComponent.Builder().build(),
            itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = 4) },
        ),
        topAxis = rememberTopAxis(
            axis = axisLineComponent(
                strokeWidth = 1.dp,
                strokeColor = Color.Black
            ), // 縦軸をはっきりさせる
            valueFormatter = { _, _ -> "" },
            guideline = null,
        ),
        endAxis = rememberEndAxis(
            axis = axisLineComponent(
                strokeWidth = 1.dp,
                strokeColor = Color.Black
            ), // 縦軸をはっきりさせる
            valueFormatter = { _, _ -> "" },
            guideline = null,
        ),
        bottomAxis = rememberBottomAxis(
            axis = axisLineComponent(
                strokeWidth = 1.dp,
                strokeColor = Color.Black
            ), // 縦軸をはっきりさせる
            valueFormatter = { value, _ ->
                (LocalDate.ofEpochDay(value.toLong())).format(
                    dateTimeFormatter
                )
            },
        ),
        getXStep = {
            when (state.duration) {
                Duration.ONE_YEAR, Duration.HALF_YEAR -> {
                    30F // 30日単位で表示する
                }

                else -> 10F
            }
        } // 横軸の直近のデータ
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun FilteringChips(
    onClickDuration: (Duration) -> Unit,
    onClickDataType: (DataType) -> Unit,
    state: GraphState.HasData,
) {
    FlowRow {
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

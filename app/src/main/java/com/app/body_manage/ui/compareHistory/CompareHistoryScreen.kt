package com.app.body_manage.ui.compareHistory

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import java.time.LocalDate

data class CompareHistoryItem(
    val date: LocalDate,
    val uri: Uri,
    val weight: Float,
)

fun createSample(weight: Float) =
    CompareHistoryItem(
        date = LocalDate.now(),
        uri = "test".toUri(),
        weight = weight
    )

data class CompareHistorySet(
    val before: CompareHistoryItem,
    val after: CompareHistoryItem,
)

data class CompareHistoryState(
    val items: List<CompareHistorySet>
)

@Composable
fun CompareHistoryScreen(
    state: CompareHistoryState
) {
    CompareHistories(state.items)
}

@Composable
private fun CompareHistories(
    histories: List<CompareHistorySet>
) {
    val configuration = LocalConfiguration.current
    LazyColumn {
        items(histories) { history ->
            CompareHistory(
                history,
                modifier = Modifier
                    .height(configuration.screenHeightDp.dp / 2)
            )
            Divider(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
                    .border(3.dp, color = Color.Gray)
            )
        }
    }
}

@Composable
private fun CompareHistory(
    history: CompareHistorySet,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.weight(1F)) {
            CompareItem(item = history.before, modifier = Modifier.weight(1F))
            CompareItem(item = history.after, modifier = Modifier.weight(1F))
        }
        DiffCompareItem(
            before = history.before,
            after = history.after,
        )
    }
}

@Composable
private fun DiffCompareItem(
    before: CompareHistoryItem,
    after: CompareHistoryItem,
) {
    Column {
        DiffItem(
            label = "日付",
            before = before.date.toString(),
            after = after.date.toString()
        )
        DiffItem(
            label = "体重",
            before = "${before.weight}kg",
            after = "${after.weight}kg"
        )
        DiffItem(
            label = "体脂肪率",
            before = "${before.weight}%",
            after = "${after.weight}%"
        )
    }
}

@Composable
private fun DiffItem(
    label: String,
    before: String,
    after: String,
) {
    Row(modifier = Modifier.padding(2.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.2F),
            fontSize = 16.sp
        )
        Text(
            text = before,
            modifier = Modifier.weight(0.4F),
            fontSize = 16.sp
        )
        Text(
            text = after,
            modifier = Modifier.weight(0.4F),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun CompareItem(
    item: CompareHistoryItem,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        AsyncImage(
            model = item.uri, contentDescription = "image",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .background(Color.Gray)
                .weight(1F)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompareHistoryScreenPreview() {
    CompareHistoryScreen(
        state = createSampleState()
    )
}

fun createSampleState(): CompareHistoryState {
    return CompareHistoryState(
        items = mutableListOf<CompareHistorySet>().apply {
            repeat(5) {
                add(
                    CompareHistorySet(
                        before = createSample(20F * it),
                        after = createSample(20F * it),
                    )
                )
            }
        }.toList()
    )
}
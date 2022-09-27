package com.app.body_manage.ui.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.style.Colors
import com.app.body_manage.util.DateUtil

@Composable
fun CompareScreen(
    bottomSheetDataList: List<BottomSheetData>,
    uiState: CompareState,
    beforeSearchLauncher: () -> Unit,
    afterSearchLauncher: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
//            FloatingActionButton(onClick = { /*TODO*/ }) {
//                Icon(
//                    imageVector = Icons.Filled.Compare,
//                    contentDescription = null
//                )
//            }
        },
        bottomBar = {
            BottomSheet(bottomSheetDataList = bottomSheetDataList)
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            when (uiState) {
                is CompareState.CompareItemsHasSet -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompareItem("Before", uiState.before, beforeSearchLauncher)
                        CompareItem("After", uiState.after, afterSearchLauncher)
                    }
                }
                is CompareState.CompareItemsError -> {

                }
            }
        }
    }
}

@Composable
private fun CompareItem(label: String, item: CompareItemStruct?, onEditClick: () -> Unit) {
    var date = "-"
    var weight = "-"
    if (item != null) {
        date = DateUtil.localDateConvertJapaneseFormatYearMonthDay(item.date)
        weight = "${item.weight}kg"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.95F)
            .height(400.dp)
            .padding(5.dp)
            .background(color = Colors.secondThemeColor, shape = RoundedCornerShape(15.dp))
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = label, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "日付")
                    Text(text = date)
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "体重")
                    Text(text = weight)
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "体型登録",
                    modifier = Modifier.clickable {
                        onEditClick.invoke()
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95F)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .fillMaxHeight(0.95F),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item?.photoUri,
                        contentScale = ContentScale.Crop,
                        contentDescription = "変更前写真"
                    )
                }
            }
        }
    }
}
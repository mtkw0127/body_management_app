package com.app.body_manage.ui.compare

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.R
import com.app.body_manage.common.BottomSheet
import com.app.body_manage.common.BottomSheetData
import com.app.body_manage.style.Colors

@Composable
fun CompareScreen(bottomSheetDataList: List<BottomSheetData>) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Compare,
                    contentDescription = null
                )
            }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompareItem()
                CompareItem()
            }

        }
    }
}

@Composable
private fun CompareItem() {
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
                Text(text = "Before")
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Text(text = "9月17日")
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Column {
                    Text(text = "体重")
                    Text(text = "63.0kg")
                }
                Divider(modifier = Modifier.fillMaxWidth(0.8F))
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "体型登録",
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
                        model = R.drawable.no_image,
                        contentScale = ContentScale.Crop,
                        contentDescription = "変更前写真"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompareItemPreview() {
    CompareItem()
}
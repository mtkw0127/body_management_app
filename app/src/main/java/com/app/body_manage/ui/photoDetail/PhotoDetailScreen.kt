package com.app.body_manage.ui.photoDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.util.DateUtil

@Composable
fun PhotoDetailScreen(
    state: PhotoDetailState,
    onClickBackPress: () -> Unit
) {
    when (state) {
        is PhotoDetailState.ShowPhotoDetail -> {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = state.photoModel.uri,
                    contentDescription = "写真詳細",
                )
            }
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 15.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        onClickBackPress()
                    }
                )
            }
            if (state.bodyMeasureModel != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "撮影日時：${DateUtil.localDateTimeToJapanese(state.bodyMeasureModel.capturedLocalDateTime)}",
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "体重：${state.bodyMeasureModel.weight}kg",
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "体脂肪率：${state.bodyMeasureModel.fat}%",
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "BMI：${state.bodyMeasureModel.bmi}",
                        color = Color.White,
                    )
                }
            }
        }

        else -> {}
    }
}

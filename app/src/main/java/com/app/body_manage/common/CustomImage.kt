package com.app.body_manage.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.data.model.Photo

@Composable
fun CustomImage(
    photo: Photo,
    size: Dp = 150.dp,
    onClickPhotoDetail: (Photo) -> Unit,
    onClickDeletePhoto: (Photo) -> Unit,
    deleteTable: Boolean = true,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    onClickPhotoDetail(photo)
                }
                .size(size),
            contentScale = ContentScale.Crop,
        )
        if (deleteTable) {
            IconButton(
                onClick = { onClickDeletePhoto(photo) },
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null
                )
            }
        }
    }
}

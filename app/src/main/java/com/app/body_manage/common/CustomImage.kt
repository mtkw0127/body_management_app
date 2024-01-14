package com.app.body_manage.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.body_manage.data.model.Photo

@Composable
fun CustomImage(
    photo: Photo,
    onClickPhotoDetail: (Photo) -> Unit,
    onClickDeletePhoto: (Photo) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            modifier = Modifier.clickable {
                onClickPhotoDetail(photo)
            }
        )
        IconButton(
            onClick = { onClickDeletePhoto(photo) },
            modifier = Modifier.offset(x = 5.dp, y = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null
            )
        }
    }
}

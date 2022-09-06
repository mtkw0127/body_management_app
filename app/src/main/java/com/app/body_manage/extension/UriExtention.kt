package com.app.body_manage.extension

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun Uri.getBitmapOrNull(contentResolver: ContentResolver): Bitmap? {
    return kotlin.runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source = ImageDecoder.createSource(contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, this)
        }
    }.getOrNull()
}
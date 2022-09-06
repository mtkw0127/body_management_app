package com.app.body_manage.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.app.body_manage.extension.getBitmapOrNull

class LocalFileRepository {
    companion object {
        private const val APP_EXTERNAL_PHOTO_DIR_NAME = "体型管理"
    }

    fun savePhotoToExternalDir(uri: Uri, context: Context) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, uri.toFile().name)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)

        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val insertedUri = resolver.insert(collection, contentValues)
        insertedUri ?: return
        resolver.openOutputStream(insertedUri).use {
            val bmp = uri.getBitmapOrNull(resolver)
            bmp ?: return@use
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(insertedUri, contentValues, null, null)
    }
}
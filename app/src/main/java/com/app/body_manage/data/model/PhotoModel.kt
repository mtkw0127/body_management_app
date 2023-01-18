package com.app.body_manage.data.model

import android.net.Uri
import com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel
import java.io.Serializable

// 撮影した写真データはここに保存する
data class PhotoModel(
    val id: Id = Id(-1),
    val uri: Uri,
    val photoType: BodyMeasureEditFormViewModel.PhotoType? = null
) {
    @JvmInline
    value class Id(val id: Int) : Serializable
}

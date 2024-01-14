package com.app.body_manage.data.model

import android.net.Uri
import java.io.Serializable

interface Photo {
    val id: Id
    val uri: Uri

    @JvmInline
    value class Id(val value: Int) : Serializable
}

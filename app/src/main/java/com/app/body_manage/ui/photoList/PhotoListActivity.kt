package com.app.body_manage.ui.photoList

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class PhotoListActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, PhotoListActivity::class.java)
    }
}
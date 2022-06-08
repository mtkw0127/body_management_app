package com.app.body_manage.ui.photoList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme

class PhotoListActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, PhotoListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = PhotoListViewModel(application = application)
        vm.loadPhotoRegisteredDates()
        setContent {
            MaterialTheme {
                PhotoListScreen(vm)
            }
        }
    }
}
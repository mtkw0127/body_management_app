package com.app.body_manage.ui.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    private val _photoList = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val photoList: LiveData<MutableList<Uri>> = _photoList

    fun addPhoto(uri: Uri) {
        _photoList.value?.add(uri)
        _photoList.postValue(_photoList.value)
    }

    fun removePhoto(position: Int) {
        _photoList.value?.removeAt(position)
        _photoList.postValue(_photoList.value)
    }

}
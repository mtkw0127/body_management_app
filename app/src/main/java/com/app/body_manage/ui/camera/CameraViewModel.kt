package com.app.body_manage.ui.camera

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class CameraViewModel : ViewModel() {
    private val _photoList = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val photoList: LiveData<MutableList<Uri>> = _photoList

    private val _canTakePhoto = MutableLiveData(true)
    val canTakePhoto: LiveData<Boolean> = _canTakePhoto

    val showNext: LiveData<Int> = _photoList.map {
        if (it.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

    fun addPhoto(uri: Uri) {
        _photoList.value?.add(uri)
        _photoList.postValue(_photoList.value)
    }

    fun removePhoto(position: Int) {
        _photoList.value?.removeAt(position)
        _photoList.postValue(_photoList.value)
    }

    fun setCanTakePhoto(takingPhoto: Boolean) {
        _canTakePhoto.value = takingPhoto
    }
}

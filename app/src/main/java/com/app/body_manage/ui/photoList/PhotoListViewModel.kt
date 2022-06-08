package com.app.body_manage.ui.photoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.body_manage.TrainingApplication
import com.app.body_manage.repository.BodyMeasurePhotoRepository
import kotlinx.coroutines.launch

class PhotoListViewModel(application: Application) : AndroidViewModel(application) {

    private val bmpRepository: BodyMeasurePhotoRepository by lazy {
        (application as TrainingApplication).bodyMeasurePhotoRepository
    }

    /**
     * 写真が登録された日、一覧を取得する
     */
    fun loadPhotoRegisteredDates() {
        viewModelScope.launch {
            kotlin.runCatching { bmpRepository.selectPhotosByDate() }
                .onFailure { e -> e.printStackTrace() }
                .onSuccess {
                    it.forEach { a ->
                        println(a)
                    }
                }
        }
    }
}

class PhotoListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoListViewModel(application = TrainingApplication()) as T
    }
}
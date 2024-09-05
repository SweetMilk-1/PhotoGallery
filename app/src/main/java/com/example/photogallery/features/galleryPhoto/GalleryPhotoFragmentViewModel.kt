package com.example.photogallery.features.galleryPhoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.photogallery.api.FlickrFetcher
import com.example.photogallery.api.model.PagedGallery.PagedGalleryResponse

class GalleryPhotoFragmentViewModel : ViewModel() {

    private val flickrFetcher: FlickrFetcher = FlickrFetcher(object:FlickrFetcher.Callbacks {
        override fun onStart() {
            _isProgressBarVisible.value = true
        }

        override fun onFinish() {
            _isProgressBarVisible.value = false
        }
    })

    private val _currentPage = MutableLiveData<Int>(1)

    private val _isProgressBarVisible = MutableLiveData(false)
    val isProgressBarVisible: LiveData<Boolean> = _isProgressBarVisible

    val photosLiveData: LiveData<PagedGalleryResponse?> = _currentPage.switchMap { page ->
        flickrFetcher.getPhoto(page)
    }

    fun nextPage() {
        val currentPage = _currentPage.value ?: 1
        _currentPage.value = currentPage + 1
    }

    fun prevPage() {
        val currentPage = _currentPage.value ?: 1
        _currentPage.value = currentPage - 1
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetcher.cancelRequests()
    }
}
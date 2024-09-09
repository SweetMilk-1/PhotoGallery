package com.example.photogallery.features.galleryPhoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.photogallery.api.FlickrFetcher
import com.example.photogallery.api.model.galleryMetadataRequest.GalleryMetadata

class GalleryPhotoFragmentViewModel : ViewModel() {

    //TODO DI
    private val flickrFetcher: FlickrFetcher = FlickrFetcher(object : FlickrFetcher.Callbacks {
        override fun onStart() {
            _isProgressBarVisible.value = true
        }

        override fun onFinish() {
            _isProgressBarVisible.value = false
        }
    })

    private val _currentPage = MutableLiveData(1)
    private val _isProgressBarVisible = MutableLiveData(false)

    val isProgressBarVisible: LiveData<Boolean> = _isProgressBarVisible
    val photosMetadata: LiveData<GalleryMetadata?> = _currentPage.switchMap { page ->
        flickrFetcher.fetchPhotosMetadata(page)
    }

    fun nextPage() {
        _currentPage.value = _currentPage.value!! + 1
    }

    fun prevPage() {
        _currentPage.value = _currentPage.value!! - 1
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetcher.cancelRequests()
    }
}
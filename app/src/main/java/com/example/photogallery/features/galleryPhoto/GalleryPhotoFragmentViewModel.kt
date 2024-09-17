package com.example.photogallery.features.galleryPhoto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.photogallery.api.FlickrFetcher
import com.example.photogallery.api.model.galleryMetadataRequest.GalleryMetadata
import com.example.photogallery.utils.QueryPreferences

data class GetPhotoParameters(
    val page: Int,
    val search: String
)

class GalleryPhotoFragmentViewModel(private val app: Application) : AndroidViewModel(app) {

    //TODO DI
    private val flickrFetcher: FlickrFetcher = FlickrFetcher(object : FlickrFetcher.Callbacks {
        override fun onStart() {
            _isProgressBarVisible.value = true
        }

        override fun onFinish() {
            _isProgressBarVisible.value = false
        }
    })

    private val _getPhotoParameters = MutableLiveData(GetPhotoParameters(
        1,
        QueryPreferences.getStoredQuery(app)
    ))
    private val getPhotoParameters
        get() = _getPhotoParameters.value ?: GetPhotoParameters(1, "")


    private val _isProgressBarVisible = MutableLiveData(false)

    val searchText
        get() = _getPhotoParameters.value?.search

    val isProgressBarVisible: LiveData<Boolean> = _isProgressBarVisible
    val photosMetadata: LiveData<GalleryMetadata?> =
        _getPhotoParameters.switchMap { getPhotoParameters ->
            if (getPhotoParameters.search.isBlank()) {
                flickrFetcher.fetchInterestingness(getPhotoParameters.page)
            } else {
                flickrFetcher.searchPhoto(getPhotoParameters.search, getPhotoParameters.page)
            }
        }

    fun nextPage() {
        _getPhotoParameters.value = getPhotoParameters.copy(page = getPhotoParameters.page + 1)
    }

    fun prevPage() {
        _getPhotoParameters.value = getPhotoParameters.copy(page = getPhotoParameters.page - 1)
    }

    fun setSearchText(text: String) {
        QueryPreferences.setStoredQuery(app, text)
        _getPhotoParameters.value = getPhotoParameters.copy(page = 1, search = text)
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetcher.cancelRequests()
    }
}
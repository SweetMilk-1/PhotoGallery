package com.example.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.model.galleryMetadataRequest.GalleryMetadata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val LOG_TAG = "FlickrFetcher"

class FlickrFetcher(
    val callbacks: Callbacks? = null
) {

    interface Callbacks {
        fun onStart()
        fun onFinish()
    }

    private val flickrApi: FlickrApi = FlickrApiFactory.create() // TODO add DI in project

    private var fetchGalleryMetadataCall: Call<GalleryMetadata>? = null

    fun fetchPhotosMetadata(page: Int): LiveData<GalleryMetadata?> {
        callbacks?.onStart()

        val photoLiveData = MutableLiveData<GalleryMetadata?>(null)

        fetchGalleryMetadataCall = flickrApi.fetchGalleryMetadata(page)
        fetchGalleryMetadataCall?.enqueue(object : Callback<GalleryMetadata> {
            override fun onResponse(
                request: Call<GalleryMetadata>,
                response: Response<GalleryMetadata>
            ) {
                val pagedGallery = response.body()
                photoLiveData.value = pagedGallery

                fetchGalleryMetadataCall = null
                callbacks?.onFinish()
            }

            override fun onFailure(request: Call<GalleryMetadata>, ex: Throwable) {
                Log.e(LOG_TAG, "Could not loading data in FlickrFetcher", ex)
                callbacks?.onFinish()
            }
        })

        return photoLiveData
    }

    @WorkerThread
    fun fetchPhotoImage(url: String): Bitmap? {
        val cacheValue = FlickrImageCache.getImage(url)
        if (cacheValue != null)
        {
            return cacheValue
        }
        val response = flickrApi.fetchPhotoImage(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.d(LOG_TAG, "Decoded bitmap=$bitmap from response=$response")

        FlickrImageCache.putImage(url, bitmap)

        return bitmap
    }

    fun cancelRequests() {
        fetchGalleryMetadataCall?.cancel()
        Log.d(LOG_TAG, "All requests has been cancelled")

        callbacks?.onFinish()
    }
}
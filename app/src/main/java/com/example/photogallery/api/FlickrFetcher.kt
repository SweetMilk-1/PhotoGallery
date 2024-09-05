package com.example.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.model.PagedGallery.PagedGalleryResponse
import com.example.photogallery.api.model.PagedGallery.PagedGalleryResponseDeserializer
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val LOG_TAG = "FlickrFetcher"

class FlickrFetcher(
    val callbacks: Callbacks? = null
) {

    interface Callbacks{
        fun onStart()
        fun onFinish()
    }

    private val flickrApi: FlickrApi // TODO add DI in project

    private var getPhotosCall: Call<PagedGalleryResponse>? = null

    init {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                PagedGalleryResponse::class.java,
                PagedGalleryResponseDeserializer()
            )
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun getPhoto(page: Int): LiveData<PagedGalleryResponse?> {
        callbacks?.onStart()
        val photoLiveData = MutableLiveData<PagedGalleryResponse?>(null)

        getPhotosCall = flickrApi.getPhoto(page)
        getPhotosCall?.enqueue(object : Callback<PagedGalleryResponse> {
            override fun onResponse(
                request: Call<PagedGalleryResponse>,
                response: Response<PagedGalleryResponse>
            ) {
                val pagedGallery = response.body()

                pagedGallery?.galleryItems = pagedGallery?.galleryItems?.filter {
                    it.url.isNotBlank()
                } ?: listOf()

                photoLiveData.value = pagedGallery
                getPhotosCall = null
                callbacks?.onFinish()
            }

            override fun onFailure(request: Call<PagedGalleryResponse>, ex: Throwable) {
                Log.e(LOG_TAG, "Could not loading data in FlickrFetcher", ex)
                callbacks?.onFinish()
            }
        })

        return photoLiveData
    }

    fun cancelRequests() {
        getPhotosCall?.cancel()
        Log.d(LOG_TAG, "All requests has been cancelled")
        callbacks?.onFinish()
    }
}
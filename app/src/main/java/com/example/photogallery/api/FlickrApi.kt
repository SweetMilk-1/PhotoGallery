package com.example.photogallery.api

import com.example.photogallery.api.model.galleryMetadataRequest.GalleryMetadata
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    @GET("/services/rest/?method=flickr.interestingness.getList")
    fun fetchInterestingness(
        @Query("page") page: Int
    ): Call<GalleryMetadata>

    @GET("/services/rest/?method=flickr.photos.search")
    fun searchPhotos(
        @Query("text") text: String,
        @Query("page") page: Int
    ): Call<GalleryMetadata>

    @GET
    fun fetchPhotoImage(@Url url: String) : Call<ResponseBody>
}


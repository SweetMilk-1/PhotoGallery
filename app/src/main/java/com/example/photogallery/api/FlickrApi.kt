package com.example.photogallery.api

import com.example.photogallery.api.model.PagedGallery.PagedGalleryResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    @GET("/services/rest/?method=flickr.galleries.getPhotos" +
            "&format=json" +
            "&extras=url_l" +
            "&nojsoncallback=1" +
            "&api_key=f4ad3b29afeae507a2462e4c61e11aa9" +
            "&gallery_id=72157722769669046" +
            "&per_page=50")
    fun fetchPhotosInfo(@Query("page") page: Int): Call<PagedGalleryResponse> //TODO Подумать как вынести секреты и фиксированые параметры в другое место Можно попробовать перенести в ресурсы

    @GET
    fun fetchPhotoImage(@Url url: String) : Call<ResponseBody>
}


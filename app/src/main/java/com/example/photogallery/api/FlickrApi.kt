package com.example.photogallery.api

import com.example.photogallery.api.model.PagedGallery.PagedGalleryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("/services/rest/?method=flickr.galleries.getPhotos" +
            "&format=json" +
            "&extras=url_l" +
            "&nojsoncallback=1" +
            "&api_key=f4ad3b29afeae507a2462e4c61e11aa9" +
            "&gallery_id=72157723046917093")
    fun getPhoto(@Query("page") page: Int): Call<PagedGalleryResponse> //TODO Подумать как вынести секреты и фиксированые параметры в другое место
}

/*
Key:
f4ad3b29afeae507a2462e4c61e11aa9

Secret:
9a5983037c63d1a2
*/
/*
https://www.flickr.com/services/rest/?method=flickr.galleries.getPhotos&format=json&nojsoncallback=1&api_key=f4ad3b29afeae507a2462e4c61e11aa9&gallery_id=72157723046917093&extras=url_l
 */
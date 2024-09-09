package com.example.photogallery.api

import com.example.photogallery.api.model.galleryMetadataRequest.GalleryMetadata
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

private const val API_KEY = "f4ad3b29afeae507a2462e4c61e11aa9"

object FlickrApiFactory {
    fun create(): FlickrApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(FlickrInterceptor())
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(
                GalleryMetadata::class.java,
                PagedGalleryResponseDeserializer()
            )
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(FlickrApi::class.java)
    }

    private class FlickrInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            val newUrl = originalRequest.url().newBuilder()
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .addQueryParameter("api_key", API_KEY)
                .build()

            val request = originalRequest.newBuilder().url(newUrl).build()

            return chain.proceed(request)
        }
    }

    private class PagedGalleryResponseDeserializer: JsonDeserializer<GalleryMetadata> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): GalleryMetadata {
            var photo : JsonObject? = null
            if (json is JsonObject) {
                photo = json.getAsJsonObject("photos")
            }
            return Gson().fromJson(photo, GalleryMetadata::class.java)
        }
    }
}
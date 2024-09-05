package com.example.photogallery.api.model.PagedGallery

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class PagedGalleryResponseDeserializer: JsonDeserializer<PagedGalleryResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PagedGalleryResponse {
        var photo : JsonObject? = null
        if (json is JsonObject) {
            photo = json.getAsJsonObject("photos")
        }
        return Gson().fromJson(photo, PagedGalleryResponse::class.java)
    }
}
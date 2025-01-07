package com.example.photogallery.api.model.galleryMetadataRequest

import android.net.Uri
import com.google.gson.annotations.SerializedName

class GalleryItem(
    var id: String,
    var title: String,
    @SerializedName("url_s") var url: String?,
    @SerializedName("owner") var owner: String = ""
) {
    val photoPageUri: Uri
        get() {
            return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}

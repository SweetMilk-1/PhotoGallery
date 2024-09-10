package com.example.photogallery.api.model.galleryMetadataRequest

import com.google.gson.annotations.SerializedName

class GalleryItem(
    var id: String,
    var title: String,
    @SerializedName("url_s") var url: String?
)

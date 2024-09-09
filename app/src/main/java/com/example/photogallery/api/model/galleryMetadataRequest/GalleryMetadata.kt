package com.example.photogallery.api.model.galleryMetadataRequest

import com.google.gson.annotations.SerializedName

class GalleryMetadata(
    @SerializedName("photo")
    var thumbnails: List<GalleryItem>,
    var page: Int,
    var pages: Int,
)



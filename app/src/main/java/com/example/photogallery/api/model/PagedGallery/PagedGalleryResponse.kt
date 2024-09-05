package com.example.photogallery.api.model.PagedGallery

import com.google.gson.annotations.SerializedName

class PagedGalleryResponse(
    @SerializedName("photo")
    var galleryItems: List<GalleryItem>,
    var page: Int,
    var pages: Int,
    var perpage: Int,
    var total: Int
)

package com.example.photogallery.api.model.PagedGallery

import com.google.gson.annotations.SerializedName

class GalleryItem(
    var id: String = "",
    var title: String = "",
    @SerializedName("url_l")
    var url: String = ""
)

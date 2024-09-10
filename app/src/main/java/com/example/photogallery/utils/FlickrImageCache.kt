package com.example.photogallery.utils

import android.graphics.Bitmap

object FlickrImageCache {
    private const val CACHE_SIZE = 4 * 1024 * 1024 //4 MiB
    private val lruCache = android.util.LruCache<String, Bitmap>(CACHE_SIZE)

    fun getImage(key: String): Bitmap? {
        synchronized (lruCache) {
            return lruCache[key]
        }
    }

    fun putImage(key: String, image: Bitmap?) {
        synchronized(lruCache) {
            lruCache.put(key, image)
        }
    }
}
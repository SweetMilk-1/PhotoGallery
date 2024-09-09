package com.example.photogallery.api

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap

private const val LOG_TAG = "ThumbnailDownloader"

private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(
    private val fragmentLifecycleOwner: LifecycleOwner,
    private val responseHandler : Handler,
    private val onThumbnailDownloaded: (T, Bitmap?) -> Unit
) : HandlerThread(LOG_TAG) {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap: ConcurrentHashMap<T, String> = ConcurrentHashMap()
    private val flickrFetcher = FlickrFetcher()

    private val fragmentLifecycleEventObserver = object :LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(LOG_TAG, "Start HandlerTread")
                    start()
                    looper
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(LOG_TAG, "Quit HandlerTread")

                    fragmentLifecycleOwner.lifecycle.removeObserver(
                        this
                    )
                    clearQueue()
                    quit()
                }
                else -> {}
            }
        }
    }

    fun clearQueue() {
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
        Log.d(LOG_TAG, "Queue was cleared")
    }

    init {
        Log.d(LOG_TAG, "Add observers into fragmentLifecycleOwner and viewLifecycleOwner")
        fragmentLifecycleOwner.lifecycle.addObserver(
            fragmentLifecycleEventObserver
        )
    }

    override fun quit(): Boolean {
        hasQuit = true
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.d(LOG_TAG, "Got a request for Url: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetcher.fetchPhotoImage(url)

        responseHandler.post {
            if (requestMap[target] != url || hasQuit) {
                return@post
            }

            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        }
    }

    fun queueThumbnail(
        target: T,
        url: String,
    ) {
        Log.d(LOG_TAG, "Got request for download image from $target")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
            .sendToTarget()
    }
}
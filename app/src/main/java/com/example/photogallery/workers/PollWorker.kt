package com.example.photogallery.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photogallery.MainActivity
import com.example.photogallery.PhotoGalleryApplication
import com.example.photogallery.R
import com.example.photogallery.api.FlickrFetcher
import com.example.photogallery.utils.QueryPreferences

private const val LOG_TAG = "PollWorker"

class PollWorker(private val context: Context, private val workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        Log.i(LOG_TAG, "PollWorker.doWork() triggered")
        val search = QueryPreferences.getStoredQuery(context)
        val lastResulId = QueryPreferences.getLastResultId(context)

        val galleryItems = if (search.isEmpty()) {
            FlickrFetcher()
                .fetchInterestingnessRequest()
                .execute()
                .body()
                ?.galleryItems
        }
        else {
            FlickrFetcher()
                .searchPhotoRequest(search)
                .execute()
                .body()
                ?.galleryItems
        } ?: emptyList()

        if (galleryItems.isEmpty()) {
            return Result.success()
        }
        val currentResultId = galleryItems.first().id
        if (lastResulId != currentResultId) {
            Log.i(LOG_TAG, "Got a new galleryItems")
            QueryPreferences.setLastResultId(context, currentResultId)

            val mainActivityIntent = MainActivity.newIntent(context)
            val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent,
                PendingIntent.FLAG_IMMUTABLE)

            val resources = context.resources
            val notification = NotificationCompat
                .Builder(context, PhotoGalleryApplication.NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.new_picture_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_picture_title))
                .setContentText(resources.getString(R.string.new_picture_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(0, notification)
        }
        return Result.success()
    }
}
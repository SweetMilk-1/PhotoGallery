package com.example.photogallery.receivers

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.photogallery.workers.PollWorker.Companion.NOTIFICATION
import com.example.photogallery.workers.PollWorker.Companion.REQUEST_CODE

private const val TAG = "NotificationReceiver"

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "received broadcast ${intent.action}")
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val requestCode = intent.getIntExtra(REQUEST_CODE, 0)
        val notification: Notification = intent.getParcelableExtra(NOTIFICATION)!!

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(requestCode, notification)
    }
}
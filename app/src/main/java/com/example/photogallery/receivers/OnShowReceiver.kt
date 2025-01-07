package com.example.photogallery.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "OnShowReceiver"

class OnShowReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "cancel notification")
        resultCode = Activity.RESULT_CANCELED
    }
}
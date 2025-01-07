package com.example.photogallery

import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.example.photogallery.receivers.OnShowReceiver
import com.example.photogallery.workers.PollWorker.Companion.ACTION_SHOW_NOTIFICATION
import com.example.photogallery.workers.PollWorker.Companion.PERM_PRIVATE

open class VisibleFragment : Fragment() {
    private var onShowReceiver = OnShowReceiver()

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowReceiver,
            filter,
            PERM_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowReceiver)
    }
}
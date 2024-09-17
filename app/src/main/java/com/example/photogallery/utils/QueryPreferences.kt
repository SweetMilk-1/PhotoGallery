package com.example.photogallery.utils

import android.content.Context
import android.preference.PreferenceManager
private const val PREF_SEARCH_STRING = "PREF_SEARCH_STRING"
private const val PREF_LAST_RESULT_ID = "PREF_LAST_RESULT_ID"
private const val PREF_POLLING = "PREF_POLLING"

object QueryPreferences {
    fun getStoredQuery(context: Context) : String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_STRING, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_STRING, query)
            .apply()
    }

    fun getLastResultId(context: Context) : String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_LAST_RESULT_ID, "")!!
    }

    fun setLastResultId(context: Context, lastResultId: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_LAST_RESULT_ID, lastResultId)
            .apply()
    }

    fun isPolling(context: Context) : Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(PREF_POLLING, false)
    }

    fun setPolling(context: Context, isPolling: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(PREF_LAST_RESULT_ID, isPolling)
            .apply()
    }
}
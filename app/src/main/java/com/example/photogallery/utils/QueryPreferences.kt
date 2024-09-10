package com.example.photogallery.utils

import android.content.Context
import android.preference.PreferenceManager
private const val PREF_SEARCH_STRING = "PREF_SEARCH_STRING"

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
}
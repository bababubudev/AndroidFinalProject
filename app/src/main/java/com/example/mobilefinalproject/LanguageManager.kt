package com.example.mobilefinalproject

import android.content.Context
import android.content.SharedPreferences

class LanguageManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("LanguageManager", Context.MODE_PRIVATE)

  fun setLanguage(language: String) {
    persistLanguage(language)
  }

  fun getLanguage(): String? {
    return prefs.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)
  }

  private fun persistLanguage(language: String) {
    prefs.edit().putString(LANGUAGE_KEY, language).apply()
  }

  companion object {
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_FINNISH = "fi"

    private const val LANGUAGE_KEY = "language_key"
    private const val PREFS_NAME = "language_prefs"
  }
}
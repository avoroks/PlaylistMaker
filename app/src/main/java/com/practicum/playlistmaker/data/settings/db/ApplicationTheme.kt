package com.practicum.playlistmaker.data.settings.db

import android.content.SharedPreferences

class ApplicationTheme(private val sharedPreferences: SharedPreferences) {
    private val DARK_MODE = "dark_mode"

    fun isDarkMode(actionForReceiveModeFromConfiguration: ()-> Boolean) = sharedPreferences.getBoolean(DARK_MODE, actionForReceiveModeFromConfiguration())

    fun saveMode(darkTheme: Boolean) =
        sharedPreferences.edit()
            .putBoolean(DARK_MODE, darkTheme)
            .apply()
}
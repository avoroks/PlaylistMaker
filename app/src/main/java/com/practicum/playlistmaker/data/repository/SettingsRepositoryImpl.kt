package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.db.ApplicationTheme
import com.practicum.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    override fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: ()-> Boolean) = ApplicationTheme(sharedPrefs).isDarkMode(actionForReceiveModeFromConfiguration)
    override fun setThemeToSharedPreferences(isDarkTheme: Boolean) =
        ApplicationTheme(sharedPrefs).saveMode(isDarkTheme)
}
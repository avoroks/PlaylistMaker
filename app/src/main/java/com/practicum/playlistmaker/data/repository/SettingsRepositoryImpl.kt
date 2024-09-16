package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.db.ApplicationTheme
import com.practicum.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    private val applicationTheme = ApplicationTheme(sharedPrefs)

    override fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: ()-> Boolean) = applicationTheme.isDarkMode(actionForReceiveModeFromConfiguration)
    override fun setThemeToSharedPreferences(isDarkTheme: Boolean) =
        applicationTheme.saveMode(isDarkTheme)
}
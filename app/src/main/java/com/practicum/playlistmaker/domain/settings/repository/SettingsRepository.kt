package com.practicum.playlistmaker.domain.settings.repository

interface SettingsRepository {
    fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: () -> Boolean): Boolean
    fun setThemeToSharedPreferences(isDarkTheme: Boolean)
}
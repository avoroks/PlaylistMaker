package com.practicum.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: ()-> Boolean) : Boolean
    fun setThemeToSharedPreferences(isDarkTheme: Boolean)
}
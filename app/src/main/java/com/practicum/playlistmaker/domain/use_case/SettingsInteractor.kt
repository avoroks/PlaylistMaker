package com.practicum.playlistmaker.domain.use_case

interface SettingsInteractor {
    fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: ()-> Boolean): Boolean
    fun setThemeToSharedPreferences(isDarkTheme: Boolean)
}
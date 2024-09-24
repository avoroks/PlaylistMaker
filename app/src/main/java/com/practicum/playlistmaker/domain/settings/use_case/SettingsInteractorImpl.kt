package com.practicum.playlistmaker.domain.settings.use_case

import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration: () -> Boolean) =
        repository.isDarkThemeInSharedPreferences(actionForReceiveModeFromConfiguration)

    override fun setThemeToSharedPreferences(isDarkTheme: Boolean) =
        repository.setThemeToSharedPreferences(isDarkTheme)
}
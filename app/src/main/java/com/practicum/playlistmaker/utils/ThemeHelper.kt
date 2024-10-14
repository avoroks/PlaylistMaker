package com.practicum.playlistmaker.utils

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ThemeHelper() : KoinComponent {
    private val settingsInteractor: SettingsInteractor by inject()
    fun setNightMode(darkThemeEnabled: Boolean) = AppCompatDelegate.setDefaultNightMode(
        if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    )

    fun isSystemDarkMode(app: Application): Boolean {
        val darkModeFlag =
            app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }

    fun applyTheme(app: Application) {
        val darkTheme = settingsInteractor.isDarkThemeInSharedPreferences { isSystemDarkMode(app) }
        setNightMode(darkTheme)
        settingsInteractor.setThemeToSharedPreferences(darkTheme)
    }
}
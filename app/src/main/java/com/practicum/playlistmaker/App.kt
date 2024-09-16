package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    var darkTheme = false
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.setApp(this)

        darkTheme = settingsInteractor.isDarkThemeInSharedPreferences { isSystemDarkMode() }
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        setNightMode()
        settingsInteractor.setThemeToSharedPreferences(darkTheme)
    }

    private fun setNightMode() = AppCompatDelegate.setDefaultNightMode(
        if (darkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO
    )

    fun isSystemDarkMode(): Boolean {
        val darkModeFlag =
            applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }
}
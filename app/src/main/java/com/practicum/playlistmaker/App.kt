package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.utils.isSystemDarkMode
import com.practicum.playlistmaker.utils.setNightMode

class App : Application() {
    var darkTheme = false
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.setApp(this)

        darkTheme = settingsInteractor.isDarkThemeInSharedPreferences { isSystemDarkMode(this) }
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        setNightMode(darkTheme)
        settingsInteractor.setThemeToSharedPreferences(darkTheme)
    }
}
package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES


private const val DARK_MODE = "dark_mode"
const val PLAYLIST_MAKER_PREFERENCES = "PLAYLIST_MAKER_PREFERENCES"

class App : Application() {
    var darkTheme = false
    private val sharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            MODE_PRIVATE
        )
    }

    override fun onCreate() {
        super.onCreate()

        darkTheme = sharedPreferences.getBoolean(DARK_MODE, isSystemDarkMode(this))
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        setNightMode()

        sharedPreferences.edit()
            .putBoolean(DARK_MODE, darkTheme)
            .apply()
    }

    private fun setNightMode() = AppCompatDelegate.setDefaultNightMode(
        if (darkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO
    )

    fun isSystemDarkMode(context: Context): Boolean {
        val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }
}
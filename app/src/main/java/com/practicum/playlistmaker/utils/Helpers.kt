package com.practicum.playlistmaker.utils

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

fun setNightMode(darkThemeEnabled: Boolean) = AppCompatDelegate.setDefaultNightMode(
    if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
)

fun isSystemDarkMode(app: Application): Boolean {
    val darkModeFlag =
        app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}
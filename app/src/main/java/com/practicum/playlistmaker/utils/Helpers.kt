package com.practicum.playlistmaker.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.internal.ViewUtils.dpToPx
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractor
import org.koin.java.KoinJavaComponent.getKoin

fun setNightMode(darkThemeEnabled: Boolean) = AppCompatDelegate.setDefaultNightMode(
    if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
)

fun isSystemDarkMode(app: Application): Boolean {
    val darkModeFlag =
        app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}

fun applyTheme(app: Application) {
    val settingsInteractor: SettingsInteractor = getKoin().get()

    val darkTheme = settingsInteractor.isDarkThemeInSharedPreferences { isSystemDarkMode(app) }
    setNightMode(darkTheme)
    settingsInteractor.setThemeToSharedPreferences(darkTheme)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Context.dpToPx(dp: Int) = dpToPx(this, dp).toInt()
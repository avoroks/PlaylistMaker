package com.practicum.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractor
import com.practicum.playlistmaker.domain.sharing.model.SharingDetails
import com.practicum.playlistmaker.domain.sharing.use_case.SharingInteractor
import com.practicum.playlistmaker.utils.ThemeHelper

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val application: Application
) : AndroidViewModel(application) {
    private val isDarkThemeSelected = MutableLiveData<Boolean>()
    fun getIsDarkThemeSelected(): LiveData<Boolean> = isDarkThemeSelected

    private val sharingDetails = MutableLiveData<SharingDetails>()
    fun getSharingDetails(): LiveData<SharingDetails> = sharingDetails

    init {
        isDarkThemeSelected.value =
            settingsInteractor.isDarkThemeInSharedPreferences { ThemeHelper().isSystemDarkMode(application) }
    }

    fun shareApp() {
        sharingDetails.value = sharingInteractor.getDetailsForSharingApp()
    }

    fun contactSupport() {
        sharingDetails.value = sharingInteractor.getDetailsForContactSupport()
    }

    fun openOffer() {
        sharingDetails.value = sharingInteractor.getDetailsForOpenOffer()
    }

    fun swithTheme(isDarkThemeSelected: Boolean) {
        this.isDarkThemeSelected.value = isDarkThemeSelected
        settingsInteractor.setThemeToSharedPreferences(isDarkThemeSelected)
        ThemeHelper().setNightMode(isDarkThemeSelected)
    }

}
package com.practicum.playlistmaker.domain.sharing.use_case

import com.practicum.playlistmaker.domain.sharing.model.SharingDetails

interface SharingInteractor {
    fun getDetailsForSharingApp(): SharingDetails
    fun getDetailsForContactSupport(): SharingDetails
    fun getDetailsForOpenOffer(): SharingDetails
}
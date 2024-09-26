package com.practicum.playlistmaker.domain.sharing.repository

import com.practicum.playlistmaker.domain.sharing.model.SharingDetails

interface SharingRepository {
    fun getDetailsForSharingApp() : SharingDetails

    fun getDetailsForContactSupport() : SharingDetails

    fun getDetailsForOpenOffer() : SharingDetails
}
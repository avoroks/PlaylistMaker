package com.practicum.playlistmaker.domain.sharing.use_case

import com.practicum.playlistmaker.domain.sharing.repository.SharingRepository

class SharingInteractorImpl(
    private val repository: SharingRepository,
) : SharingInteractor {
    override fun getDetailsForSharingApp() = repository.getDetailsForSharingApp()

    override fun getDetailsForContactSupport() = repository.getDetailsForContactSupport()

    override fun getDetailsForOpenOffer() = repository.getDetailsForOpenOffer()
}

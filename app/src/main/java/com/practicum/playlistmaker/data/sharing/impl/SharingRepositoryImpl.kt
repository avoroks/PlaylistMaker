package com.practicum.playlistmaker.data.sharing.impl

import android.app.Application
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.model.ActionType
import com.practicum.playlistmaker.domain.sharing.model.ExtraDataForSharing
import com.practicum.playlistmaker.domain.sharing.model.SharingDetails
import com.practicum.playlistmaker.domain.sharing.repository.SharingRepository

class SharingRepositoryImpl(private val app: Application) : SharingRepository {
    override fun getDetailsForSharingApp() = SharingDetails(
        actionType = ActionType.SHARE_APP,
        extraData = ExtraDataForSharing.ExtraDataForSharingApp(appLink = app.getString(R.string.course_link))
    )

    override fun getDetailsForContactSupport() = SharingDetails(
        actionType = ActionType.CONTACT_SUPPORT,
        extraData = ExtraDataForSharing.ExtraDataForContactSupport(
            email = app.getString(R.string.email),
            subject = app.getString(R.string.email_subject),
            text = app.getString(R.string.email_text)
        )
    )

    override fun getDetailsForOpenOffer() = SharingDetails(
        actionType = ActionType.OPEN_OFFER,
        extraData = ExtraDataForSharing.ExtraDataForOpenOffer(offerLink = app.getString(R.string.practicum_offer))
    )
}
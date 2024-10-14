package com.practicum.playlistmaker.data.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.model.ActionType
import com.practicum.playlistmaker.domain.sharing.model.ExtraDataForSharing
import com.practicum.playlistmaker.domain.sharing.model.SharingDetails
import com.practicum.playlistmaker.domain.sharing.repository.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getDetailsForSharingApp() = SharingDetails(
        actionType = ActionType.SHARE_APP,
        extraData = ExtraDataForSharing.ExtraDataForSharingApp(appLink = context.getString(R.string.course_link))
    )

    override fun getDetailsForContactSupport() = SharingDetails(
        actionType = ActionType.CONTACT_SUPPORT,
        extraData = ExtraDataForSharing.ExtraDataForContactSupport(
            email = context.getString(R.string.email),
            subject = context.getString(R.string.email_subject),
            text = context.getString(R.string.email_text)
        )
    )

    override fun getDetailsForOpenOffer() = SharingDetails(
        actionType = ActionType.OPEN_OFFER,
        extraData = ExtraDataForSharing.ExtraDataForOpenOffer(offerLink = context.getString(R.string.practicum_offer))
    )
}
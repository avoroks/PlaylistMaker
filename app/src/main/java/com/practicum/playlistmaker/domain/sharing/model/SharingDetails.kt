package com.practicum.playlistmaker.domain.sharing.model

data class SharingDetails(
    val actionType: ActionType,
    val extraData: ExtraDataForSharing
)

enum class ActionType {
    SHARE_APP,
    CONTACT_SUPPORT,
    OPEN_OFFER
}

sealed class ExtraDataForSharing {
    data class ExtraDataForSharingApp(val appLink: String) : ExtraDataForSharing()
    data class ExtraDataForContactSupport(
        val email: String,
        val subject: String,
        val text: String
    ) : ExtraDataForSharing()
    data class ExtraDataForOpenOffer(val offerLink: String) : ExtraDataForSharing()
}
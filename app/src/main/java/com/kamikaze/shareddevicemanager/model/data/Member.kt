package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.DocumentId

data class Member(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val role: Role? = null
) {

    val canDelete: Boolean
        get() = (role != Role.OWNER)

    enum class Role {
        OWNER,
        ADMIN,
        GENERAL
    }
}

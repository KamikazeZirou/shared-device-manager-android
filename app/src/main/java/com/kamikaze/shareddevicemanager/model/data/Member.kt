package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.DocumentId

data class Member(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val role: Role? = null
) {

    enum class Role {
        OWNER,
        ADMIN,
        GENERAL
    }
}

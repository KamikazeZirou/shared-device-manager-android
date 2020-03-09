package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Group(@DocumentId val id: String? = null,
                 val name: String? = null,
                 val owner: String? = null,
                 val default: Boolean? = null,
                 val members: List<String>? = null)

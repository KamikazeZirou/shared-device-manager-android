package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Group(@get:Exclude var id: String? = null,
                 val name: String? = null,
                 val owner: String? = null,
                 val default: Boolean? = null)
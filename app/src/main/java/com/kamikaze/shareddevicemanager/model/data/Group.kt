package com.kamikaze.shareddevicemanager.model.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Group(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val owner: String? = null,
    // FIXME Firestoreを使う都合上、存在するフィールドなので隠蔽したい
    val members: List<String>? = null,
    val default: Boolean? = null
) : Parcelable

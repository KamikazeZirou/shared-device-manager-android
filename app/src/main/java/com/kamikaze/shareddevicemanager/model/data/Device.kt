package com.kamikaze.shareddevicemanager.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device(
    val id: Long = -1,
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val isTablet: Boolean = false,
    val osVersion: String = "",
    val status: Status = Status.FREE,
    val user: String = "",
    val issueDate: String = "",
    val estimatedReturnDate: String = "",
    val returnDate: String = "",
    val registerDate: String = ""
) : Parcelable {
    val os: String = "Android"

    enum class Status {
        FREE,
        IN_USE,
        DISPOSAL,
    }
}


package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Device(
    @get:Exclude var id: String = "",
    val instanceId: String = "ID of phone or tablet", // TODO
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val isTablet: Boolean = false,
    val os: String = "Android",
    val osVersion: String = "",
    val status: Status = Status.UNKNOWN,
    val user: String = "",
    val issueDate: String = "",
    val estimatedReturnDate: String = "",
    val returnDate: String = "",
    val registerDate: String = "",
    val disposalDate: String = ""
) {
    @get:Exclude
    val readableOS: String
        get() = "%s %s".format(this.os, this.osVersion)

    enum class Status(val canLink: Boolean, val isRegistered: Boolean) {
        UNKNOWN(canLink = false, isRegistered = false),
        NOT_REGISTER(canLink = false, isRegistered = false),
        FREE(canLink = true, isRegistered = true),
        IN_USE(canLink = true, isRegistered = true),
        DISPOSAL(canLink = false, isRegistered = true)
    }
}

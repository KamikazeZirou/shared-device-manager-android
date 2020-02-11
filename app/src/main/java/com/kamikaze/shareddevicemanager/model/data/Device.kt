package com.kamikaze.shareddevicemanager.model.data

data class Device(
    val id: Long = -1,
    val instanceId: String = "ID of phone or tablet", // TODO
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val isTablet: Boolean = false,
    val osVersion: String = "",
    val status: Status = Status.UNKNOWN,
    val user: String = "",
    val issueDate: String = "",
    val estimatedReturnDate: String = "",
    val returnDate: String = "",
    val registerDate: String = "",
    val disposalDate: String = ""
) {
    val os: String = "Android"

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

package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.kamikaze.shareddevicemanager.util.todayStr

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
    fun register(): Device = this.copy(
        instanceId = instanceId,
        status = Status.FREE,
        registerDate = todayStr()
    )

    fun borrow(user: String, estimatedReturnDate: String): Device = this.copy(
        user = user,
        estimatedReturnDate = estimatedReturnDate,
        status = Status.IN_USE,
        issueDate = todayStr()
    )

    fun `return`(): Device = this.copy(
        status = Status.FREE,
        returnDate = todayStr()
    )

    fun linkTo(device: Device): Device = device.copy(
        instanceId = this.instanceId,
        model = this.model,
        manufacturer = this.manufacturer,
        osVersion = this.osVersion
    )

    fun dispose(): Device = this.copy(
        status = Status.DISPOSAL,
        disposalDate = todayStr()
    )

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

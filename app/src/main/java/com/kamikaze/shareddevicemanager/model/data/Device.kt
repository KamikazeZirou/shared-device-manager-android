package com.kamikaze.shareddevicemanager.model.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Device(
    @DocumentId val id: String = "",
    val instanceId: String = "ID of phone or tablet", // TODO
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    @field:JvmField val isTablet: Boolean = false,
    val os: String = "Android",
    val osVersion: String = "",
    val status: Status = Status.UNKNOWN,
    val user: String = "",
    val issueDate: Date? = null,
    val estimatedReturnDate: Date? = null,
    val returnDate: Date? = null,
    val registerDate: Date? = null,
    val disposalDate: Date? = null
) {
    companion object {
        private const val NAME_MAX_LENGTH = 80
        private const val USER_NAME_MAX_LENGTH = 40

        fun validateName(name: String): Boolean =
            when {
                name.isEmpty() -> false
                name.length > NAME_MAX_LENGTH -> false
                else -> true
            }

        fun validateUserName(userName: String): Boolean =
            when {
                userName.isEmpty() -> false
                userName.length > USER_NAME_MAX_LENGTH -> false
                else -> true
            }

        fun validateEstimatedReturnDate(date: Date): Boolean {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.clear()
            calendar.set(year, month, day)
            return date >= calendar.time
        }
    }

    fun register(name: String): Device = this.copy(
        name = name,
        status = Status.FREE,
        registerDate = Date()
    )

    fun borrow(user: String, estimatedReturnDate: Date): Device = this.copy(
        user = user,
        estimatedReturnDate = estimatedReturnDate,
        status = Status.IN_USE,
        issueDate = Date()
    )

    fun `return`(): Device = this.copy(
        status = Status.FREE,
        returnDate = Date()
    )

    fun linkTo(device: Device): Device = device.copy(
        instanceId = this.instanceId,
        model = this.model,
        manufacturer = this.manufacturer,
        osVersion = this.osVersion
    )

    fun dispose(): Device = this.copy(
        status = Status.DISPOSAL,
        disposalDate = Date()
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

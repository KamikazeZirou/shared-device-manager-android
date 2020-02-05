package com.kamikaze.shareddevicemanager.model.data

data class Device(
    val id: Long = -1,
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val isPhone: Boolean = true,
    val os: String = "Android",
    val osVersion: String = "",
    val status: Status = Status.FREE,
    val user: String = "",
    val issueDate: String = "",
    val estimatedReturnDate: String = "",
    val returnDate: String = "",
    val registerDate: String = ""
) {
    enum class Status {
        FREE,
        IN_USE,
        DISPOSAL,
    }
}


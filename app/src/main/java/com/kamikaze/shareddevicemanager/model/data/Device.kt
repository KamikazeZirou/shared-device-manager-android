package com.kamikaze.shareddevicemanager.model.data

data class Device(
    var id: Long = -1,
    val name: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val isPhone: Boolean = true,
    val os: String = "Android",
    val osVersion: String = "",
    val inUse: Boolean = false,
    val user: String = "",
    val issueDate: String = "",
    val estimatedReturnDate: String = "",
    val registerDate: String = ""
) {}

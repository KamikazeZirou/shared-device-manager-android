package com.kamikaze.shareddevicemanager.ui.util

fun String.toVisibleStr(): String =
    if (this.isNullOrEmpty()) {
        "---"
    } else {
        this
    }
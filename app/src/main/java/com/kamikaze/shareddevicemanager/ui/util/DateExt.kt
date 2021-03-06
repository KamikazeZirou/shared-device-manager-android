package com.kamikaze.shareddevicemanager.ui.util

import java.text.SimpleDateFormat
import java.util.*


private val SDF = SimpleDateFormat("yyyy/MM/dd", Locale.US)

fun Date?.toVisibleStr(): String =
    if (this != null) {
        SDF.format(this)
    } else {
        "---"
    }

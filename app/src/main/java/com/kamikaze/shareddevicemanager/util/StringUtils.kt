package com.kamikaze.shareddevicemanager.util

import java.util.*


fun todayStr(): String {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH) + 1
    val day = c.get(Calendar.DAY_OF_MONTH)
    return "%04d/%02d/%02d".format(year, month, day)
}
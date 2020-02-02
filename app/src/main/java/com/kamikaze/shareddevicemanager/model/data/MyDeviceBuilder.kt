package com.kamikaze.shareddevicemanager.model.data

import android.os.Build

class MyDeviceBuilder : IMyDeviceBuilder {
    override fun build(name: String): Device {
        return Device(name = name,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            isPhone = true,
            osVersion = Build.VERSION.RELEASE)
    }
}

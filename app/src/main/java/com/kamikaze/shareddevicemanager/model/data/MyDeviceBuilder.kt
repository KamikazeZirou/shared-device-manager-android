package com.kamikaze.shareddevicemanager.model.data

import android.content.Context
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDeviceBuilder @Inject constructor(context: Context) : IMyDeviceBuilder {
    override fun build(name: String): Device {
        return Device(
            name = name,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            isPhone = true,
            osVersion = Build.VERSION.RELEASE
        )
    }
}

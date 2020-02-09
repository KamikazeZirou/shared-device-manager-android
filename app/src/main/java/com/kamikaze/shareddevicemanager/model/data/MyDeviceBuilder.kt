package com.kamikaze.shareddevicemanager.model.data

import android.content.Context
import android.os.Build
import com.kamikaze.shareddevicemanager.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDeviceBuilder @Inject constructor(private val context: Context) : IMyDeviceBuilder {
    override fun build(name: String): Device {
        return Device(
            name = name,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            isTablet = context.resources.getBoolean(R.bool.is_tablet),
            osVersion = Build.VERSION.RELEASE
        )
    }
}

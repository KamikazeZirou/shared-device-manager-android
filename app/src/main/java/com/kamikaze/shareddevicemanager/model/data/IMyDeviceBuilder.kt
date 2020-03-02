package com.kamikaze.shareddevicemanager.model.data

interface IMyDeviceBuilder {
    suspend fun build(name: String = ""): Device
}

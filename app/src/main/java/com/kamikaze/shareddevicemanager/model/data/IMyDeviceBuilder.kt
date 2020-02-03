package com.kamikaze.shareddevicemanager.model.data

interface IMyDeviceBuilder {
    fun build(name: String = ""): Device
}

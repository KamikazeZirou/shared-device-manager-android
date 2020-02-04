package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    fun register(device: Device)
    fun borrow(device: Device)
    suspend fun returnDevice()

    val myDeviceFlow: Flow<Device>
    val devicesFlow: Flow<List<Device>>
    val deviceRegisteredFlow: Flow<Boolean>
}

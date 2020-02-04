package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    suspend fun register(device: Device)
    suspend fun borrow(device: Device)
    suspend fun returnDevice()

    val myDeviceFlow: Flow<Device>
    val devicesFlow: Flow<List<Device>>
    val deviceRegisteredFlow: Flow<Boolean>
}

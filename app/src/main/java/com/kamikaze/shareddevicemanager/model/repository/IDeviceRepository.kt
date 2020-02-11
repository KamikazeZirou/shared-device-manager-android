package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    suspend fun get(deviceId: Long) : Device
    suspend fun register(device: Device)
    suspend fun borrow(device: Device)
    suspend fun returnDevice()
    suspend fun linkDevice(targetDeviceId: Long)
    suspend fun dispose()

    val myDeviceFlow: Flow<Device>
    val devicesFlow: Flow<List<Device>>
}

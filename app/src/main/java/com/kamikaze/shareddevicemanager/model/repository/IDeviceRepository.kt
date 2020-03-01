package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    suspend fun setGroup(group: Group?)
    suspend fun get(deviceId: String) : Device
    suspend fun register(device: Device): Device
    suspend fun borrow(device: Device)
    suspend fun returnDevice(device: Device)
    suspend fun linkDevice(device: Device, targetDeviceId: String)
    suspend fun dispose(device: Device)

    val myDeviceFlow: Flow<Device>
    val devicesFlow: Flow<List<Device>>
}

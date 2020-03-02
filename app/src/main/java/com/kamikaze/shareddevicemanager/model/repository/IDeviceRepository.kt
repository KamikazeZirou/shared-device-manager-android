package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    suspend fun setGroup(group: Group?)
    suspend fun add(device: Device): Device
    suspend fun get(deviceId: String) : Device
    suspend fun update(device: Device): Device

    val myDeviceFlow: Flow<Device>
    val devicesFlow: Flow<List<Device>>
}

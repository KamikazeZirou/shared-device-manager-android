package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.flow.Flow

interface IDeviceRepository {
    fun add(groupId: String, device: Device)
    fun get(groupId: String) : Flow<List<Device>?>
    fun getByInstanceId(groupId: String, instanceId: String): Flow<Device?>
    fun getById(groupId: String, deviceId: String) : Flow<Device?>
    fun update(groupId: String, device: Device)
}

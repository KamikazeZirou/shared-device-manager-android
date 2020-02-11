package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDeviceRepository @Inject constructor(deviceBuilder: IMyDeviceBuilder) :
    IDeviceRepository {
    private val devices = mutableListOf<Device>()

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val deviceRegisteredChannel = ConflatedBroadcastChannel<Boolean>(false)
    override val deviceRegisteredFlow: Flow<Boolean> = deviceRegisteredChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>(deviceBuilder.build())
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()

    init {
        devices += (1..25).map {
            Device(
                id = it.toLong(),
                name = "Device $it",
                model = "Model $it",
                manufacturer = "manufacturer $it",
                isTablet = (it % 2 == 0),
                osVersion = "8.0.0",
                status = (Device.Status.values()[it % 3]),
                user = "user $it",
                issueDate = "2020/02/03",
                estimatedReturnDate = "2020/02/04",
                returnDate = "2020/02/03",
                registerDate = "2020/02/01"
            )
        }

        GlobalScope.launch {
            devicesChannel.send(devices)
        }
    }

    override suspend fun get(deviceId: Long): Device {
        return devices.find { it.id == deviceId }!!
    }

    override suspend fun register(device: Device) {
        // 使い方によっては、異なるデバイスに同じIDを振ってしまうが、Fake実装なので許容
        val registeredDevice = device.copy(
            id = devices.size.toLong() + 1,
            registerDate = todayStr()
        )
        devices += registeredDevice

        myDeviceChannel.send(registeredDevice)
        devicesChannel.send(devices.toList())
        deviceRegisteredChannel.send(true)
    }

    override suspend fun borrow(device: Device) {
        val updatedDevice = device.copy(
            status = Device.Status.IN_USE,
            issueDate = todayStr()
        )
        myDeviceChannel.send(updatedDevice)
        updateDevice(updatedDevice)
    }

    override suspend fun returnDevice() {
        val updatedDevice = myDeviceChannel.value.copy(
            status = Device.Status.FREE,
            returnDate = todayStr()
        )
        myDeviceChannel.send(updatedDevice)
        updateDevice(updatedDevice)
    }

    override suspend fun linkDevice(targetDeviceId: Long) {
        val index = devices.indexOfFirst { it.id == targetDeviceId }
        if (index == -1) {
            return
        }

        val myDevice = myDeviceChannel.value
        val updatedDevice = devices[index].copy(
            instanceId = myDevice.instanceId,
            model = myDevice.model,
            manufacturer = myDevice.manufacturer,
            osVersion = myDevice.osVersion
        )

        myDeviceChannel.send(updatedDevice)
        updateDevice(updatedDevice)
        deviceRegisteredChannel.send(true)
    }

    private suspend fun updateDevice(device: Device) {
        devices
            .indexOfFirst { it.id == device.id }
            .takeIf { it != -1 }
            ?.let {
                devices[it] = device
                devicesChannel.send(devices.toList())
            }
    }

    private fun todayStr(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        return "%04d/%02d/%02d".format(year, month, day)
    }
}

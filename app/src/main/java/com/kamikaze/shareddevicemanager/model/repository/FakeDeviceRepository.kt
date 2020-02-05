package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import java.util.*

class FakeDeviceRepository() : IDeviceRepository {
    companion object {
        val instance: FakeDeviceRepository = FakeDeviceRepository()
    }

    private val devices = mutableListOf<Device>()

    init {
        devices += (1..25).map {
            Device(id = it.toLong(), name = "Device $it")
        }

        GlobalScope.launch {
            devicesChannel.send(devices)
        }
    }

    override suspend fun register(device: Device) {
        // 使い方によっては、異なるデバイスに同じIDを振ってしまうが、Fake実装なので許容
        val registeredDevice = device.copy(
            id = devices.size.toLong() + 1,
            registerDate = todayStr()
        )
        devices += registeredDevice

        myDeviceChannel.send(registeredDevice)
        devicesChannel.send(devices)
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

    private suspend fun updateDevice(device: Device) {
        devices
            .indexOfFirst { it.id == device.id }
            .takeIf { it != -1 }
            ?.let {
                devices[it] = device
                devicesChannel.send(devices)
            }
    }

    private fun todayStr(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        return "%04d/%02d/%02d".format(year, month, day)
    }

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val deviceRegisteredChannel = ConflatedBroadcastChannel<Boolean>(false)
    override val deviceRegisteredFlow: Flow<Boolean> = deviceRegisteredChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>()
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()
}
